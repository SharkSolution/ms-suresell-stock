package org.blackequity.printer.service;



import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.event.Observes;
import org.blackequity.printer.config.PrinterConnector;
import org.blackequity.printer.domain.PrinterStatus;
import org.blackequity.printer.domain.TicketItem;
import org.blackequity.printer.domain.TicketRequest;
import org.blackequity.printer.util.EscPosCommands;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import jakarta.enterprise.context.ApplicationScoped;

import java.io.IOException;
import java.io.OutputStream;

@ApplicationScoped
public class PrinterService {

    private static final Logger LOGGER = Logger.getLogger(PrinterService.class);

    @ConfigProperty(name = "printer.port", defaultValue = "COM1")
    String printerPort;

    @ConfigProperty(name = "printer.name", defaultValue = "DIG-58iiA")
    String printerName;

    private PrinterConnector printerConnector;
    private EscPosCommands escPosCommands;

    void onStart(@Observes StartupEvent ev) {
        LOGGER.info("Inicializando servicio de impresión para " + printerName + " en puerto " + printerPort);
        this.printerConnector = new PrinterConnector(printerPort);
        this.escPosCommands = new EscPosCommands();

        try {
            this.printerConnector.connect();
            LOGGER.info("Conexión con impresora establecida correctamente");
        } catch (IOException e) {
            LOGGER.error("No se pudo establecer conexión con la impresora", e);
        }
    }

    void onStop(@Observes ShutdownEvent ev) {
        LOGGER.info("Cerrando servicio de impresión");
        try {
            if (this.printerConnector != null) {
                this.printerConnector.disconnect();
            }
        } catch (IOException e) {
            LOGGER.error("Error al cerrar conexión con impresora", e);
        }
    }

    public void printTicket(TicketRequest ticketRequest) throws IOException {
        LOGGER.info("Imprimiendo ticket para: " + ticketRequest.getBusinessName());



        try (OutputStream os = printerConnector.getOutputStream()) {
            // Inicializar impresora
            os.write(escPosCommands.initPrinter());

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            // Cabecera del ticket
            os.write(escPosCommands.setAlignment(EscPosCommands.ALIGN_CENTER));
            os.write(escPosCommands.setBold(true));
            os.write(escPosCommands.setFontSize(0, 1));
            os.write((ticketRequest.getBusinessName() + "\n").getBytes());
            os.write(escPosCommands.setBold(false));
            os.write(escPosCommands.setFontSize(0, 0));
            os.write((ticketRequest.getAddress() + "\n").getBytes());
            os.write(("Tel: " + ticketRequest.getPhone() + "\n").getBytes());
            os.write(("RFC: " + ticketRequest.getTaxId() + "\n\n").getBytes());

            // Fecha y número de ticket
            os.write(escPosCommands.setAlignment(EscPosCommands.ALIGN_LEFT));
            os.write(("Fecha: " + ticketRequest.getDateTime() + "\n").getBytes());
            os.write(("Ticket #: " + ticketRequest.getTicketNumber() + "\n").getBytes());
            os.write(("Cajero: " + ticketRequest.getCashierName() + "\n\n").getBytes());

            // Detalle de productos
            os.write(escPosCommands.setBold(true));
            os.write("PRODUCTO      CANT   PRECIO   TOTAL\n".getBytes());
            os.write(escPosCommands.setBold(false));
            os.write("--------------------------------\n".getBytes());

            for (TicketItem item : ticketRequest.getItems()) {
                // Formatear para columnas fijas en base al ancho de 48mm
                String line = String.format("%-14s %2d %8.2f %8.2f\n",
                        truncate(item.getName(), 14),
                        item.getQuantity(),
                        item.getUnitPrice(),
                        item.getTotal());
                os.write(line.getBytes());
            }

            os.write("--------------------------------\n".getBytes());

            // Totales
            os.write(escPosCommands.setAlignment(EscPosCommands.ALIGN_RIGHT));
            os.write(("SUBTOTAL: $" + String.format("%.2f", ticketRequest.getSubtotal()) + "\n").getBytes());
            os.write(("IVA: $" + String.format("%.2f", ticketRequest.getTax()) + "\n").getBytes());
            os.write(escPosCommands.setBold(true));
            os.write(("TOTAL: $" + String.format("%.2f", ticketRequest.getTotal()) + "\n\n").getBytes());
            os.write(escPosCommands.setBold(false));

            // Forma de pago
            os.write(escPosCommands.setAlignment(EscPosCommands.ALIGN_LEFT));
            os.write(("Forma de pago: " + ticketRequest.getPaymentMethod() + "\n").getBytes());

            // Pie de ticket
            os.write(escPosCommands.setAlignment(EscPosCommands.ALIGN_CENTER));
            os.write("\nGRACIAS POR SU COMPRA\n\n".getBytes());

            // Imprimir código de barras o QR si es necesario
            if (ticketRequest.getQrContent() != null && !ticketRequest.getQrContent().isEmpty()) {
                os.write(escPosCommands.printQRCode(ticketRequest.getQrContent()));
                os.write("\n".getBytes());
            }

            os.write(escPosCommands.feedLines(13));
            os.write(".\n".getBytes());
            openCashDrawer();

            // Cortar papel
            os.write(escPosCommands.cutPaper());
            os.flush();

            LOGGER.info("Ticket impreso correctamente");
        }
    }

    public void printTestTicket() throws IOException {
        LOGGER.info("Imprimiendo ticket de prueba");

        try (OutputStream os = printerConnector.getOutputStream()) {
            // Inicializar impresora
            os.write(escPosCommands.initPrinter());

            // Contenido de prueba
            os.write(escPosCommands.setAlignment(EscPosCommands.ALIGN_CENTER));
            os.write(escPosCommands.setBold(true));
            os.write("*** TICKET DE PRUEBA ***\n\n".getBytes());
            os.write(escPosCommands.setBold(false));

            os.write("Impresora configurada correctamente\n".getBytes());
            os.write(("Modelo: " + printerName + "\n").getBytes());
            os.write(("Puerto: " + printerPort + "\n\n").getBytes());

            // Muestra de formatos
            os.write(escPosCommands.setAlignment(EscPosCommands.ALIGN_LEFT));
            os.write("FORMATOS DISPONIBLES:\n".getBytes());
            os.write("--------------------------------\n".getBytes());

            os.write(escPosCommands.setBold(true));
            os.write("Texto en negrita\n".getBytes());
            os.write(escPosCommands.setBold(false));

            os.write(escPosCommands.setFontSize(1, 1));
            os.write("Texto más grande\n".getBytes());
            os.write(escPosCommands.setFontSize(0, 0));

            os.write(escPosCommands.setUnderline(true));
            os.write("Texto subrayado\n".getBytes());
            os.write(escPosCommands.setUnderline(false));

            os.write(escPosCommands.setAlignment(EscPosCommands.ALIGN_CENTER));
            os.write("Texto centrado\n".getBytes());

            os.write(escPosCommands.setAlignment(EscPosCommands.ALIGN_RIGHT));
            os.write("Texto a la derecha\n".getBytes());

            os.write(escPosCommands.setAlignment(EscPosCommands.ALIGN_LEFT));
            os.write("Texto a la izquierda\n\n".getBytes());

            // Códigos de barras/QR de prueba
            os.write(escPosCommands.setAlignment(EscPosCommands.ALIGN_CENTER));
            os.write("CÓDIGO QR DE PRUEBA:\n".getBytes());
            os.write(escPosCommands.printQRCode("https://www.example.com"));
            os.write("\n\n".getBytes());

            os.write(escPosCommands.feedLines(13));
            os.write(".\n".getBytes());
            openCashDrawer();

            // Cortar papel
            os.write(escPosCommands.cutPaper());
            os.flush();

            LOGGER.info("Ticket de prueba impreso correctamente");
        }
    }

    public PrinterStatus getPrinterStatus() throws IOException {
        LOGGER.info("Consultando estado de la impresora");

        boolean isConnected = printerConnector.isConnected();
        boolean hasPaper = true;  // En implementaciones reales, consultar estado del papel

        return new PrinterStatus(isConnected, printerName, printerPort, hasPaper);
    }

    public void openCashDrawer() throws IOException {
        LOGGER.info("Abriendo cajón monedero SAT 119X");

        try (OutputStream os = printerConnector.getOutputStream()) {
            // Enviar comando para abrir cajón
            os.write(escPosCommands.openCashDrawer());
            os.flush();

            LOGGER.info("Comando de apertura enviado al cajón monedero");
        }
    }

    private String truncate(String text, int length) {
        if (text == null) return "";
        return text.length() > length ? text.substring(0, length) : text;
    }

}
