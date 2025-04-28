package org.blackequity.printer.config;

import org.jboss.logging.Logger;

import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.SimpleDoc;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Implementación para SAT q22 en Windows
 */
public class PrinterConnector {

    private static final Logger LOGGER = Logger.getLogger(PrinterConnector.class);

    private String printerName;
    private PrintService printService;
    private WindowsPrinterOutputStream outputStream;
    private AtomicBoolean connected = new AtomicBoolean(false);

    public PrinterConnector(String printerPort) {
        this.printerName = printerPort;
    }

    public void connect() throws IOException {
        if (connected.get()) {
            LOGGER.warn("La impresora ya está conectada");
            return;
        }

        LOGGER.info("Conectando a impresora SAT q22 en Windows");

        try {
            printService = findPrintService(printerName);

            if (printService == null) {
                LOGGER.warn("No se encontró la impresora con nombre: " + printerName + ". Intentando usar la impresora por defecto.");
                printService = PrintServiceLookup.lookupDefaultPrintService();

                if (printService == null) {
                    throw new IOException("No se encontró ninguna impresora disponible");
                }
            }

            LOGGER.info("Impresora encontrada: " + printService.getName());

            outputStream = new WindowsPrinterOutputStream(printService);

            connected.set(true);
            LOGGER.info("Conexión establecida con impresora " + printService.getName());
        } catch (Exception e) {
            LOGGER.error("Error al conectar con impresora", e);
            throw new IOException("No se pudo conectar con la impresora: " + e.getMessage(), e);
        }
    }

    /**
     * Cierra la conexión con la impresora
     */
    public void disconnect() throws IOException {
        if (!connected.get()) {
            LOGGER.warn("La impresora no está conectada");
            return;
        }

        LOGGER.info("Desconectando impresora");

        try {
            if (outputStream != null) {
                outputStream.close();
                outputStream = null;
            }
            connected.set(false);
            LOGGER.info("Impresora desconectada correctamente");
        } catch (Exception e) {
            LOGGER.error("Error al desconectar impresora", e);
            throw new IOException("Error al desconectar: " + e.getMessage(), e);
        }
    }

    public OutputStream getOutputStream() throws IOException {
        if (!connected.get() || outputStream == null) {
            connect();
        }
        return outputStream;
    }

    /**
     * Verifica si hay conexión con la impresor
     */
    public boolean isConnected() {
        return connected.get() && outputStream != null;
    }

    /**
     * Busca un servicio de impresión por nombre
     */
    private PrintService findPrintService(String printerName) {
        PrintService[] printServices = PrintServiceLookup.lookupPrintServices(null, null);
        LOGGER.info("Impresoras encontradas en el sistema: " + printServices.length);

        for (PrintService service : printServices) {
            LOGGER.info("- " + service.getName());

            if (service.getName().toLowerCase().contains(printerName.toLowerCase()) ||
                    service.getName().equalsIgnoreCase(printerName)) {
                return service;
            }
        }

        return null;
    }

    /**
     * Clase personalizada de OutputStream para impresoras en Windows
     */
    private class WindowsPrinterOutputStream extends OutputStream {
        private PrintService printService;
        private byte[] buffer;
        private int position;
        private static final int BUFFER_SIZE = 8192;

        public WindowsPrinterOutputStream(PrintService printService) {
            this.printService = printService;
            this.buffer = new byte[BUFFER_SIZE];
            this.position = 0;
        }

        @Override
        public void write(int b) throws IOException {
            if (position >= BUFFER_SIZE) {
                flush();
            }

            buffer[position++] = (byte) b;
        }

        @Override
        public void write(byte[] b, int off, int len) throws IOException {
            if (position + len > BUFFER_SIZE) {
                flush();

                // Si es más grande que el buffer, enviamos directamente
                if (len > BUFFER_SIZE) {
                    byte[] temp = new byte[len];
                    System.arraycopy(b, off, temp, 0, len);
                    sendToPrinter(temp);
                    return;
                }
            }

            System.arraycopy(b, off, buffer, position, len);
            position += len;
        }

        @Override
        public void flush() throws IOException {
            if (position > 0) {
                byte[] temp = new byte[position];
                System.arraycopy(buffer, 0, temp, 0, position);
                sendToPrinter(temp);
                position = 0;
            }
        }

        @Override
        public void close() throws IOException {
            flush();
        }

        private void sendToPrinter(byte[] data) throws IOException {
            try {
                DocPrintJob job = printService.createPrintJob();
                Doc doc = new SimpleDoc(data, DocFlavor.BYTE_ARRAY.AUTOSENSE, null);
                PrintRequestAttributeSet attributes = new HashPrintRequestAttributeSet();
                job.print(doc, attributes);

                // Pequeña pausa para asegurar que la impresora procese los datos
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            } catch (Exception e) {
                throw new IOException("Error al enviar datos a la impresora: " + e.getMessage(), e);
            }
        }
    }

    public java.io.InputStream getInputStream() throws IOException {
        // En la implementación de Windows con javax.print, la comunicación bidireccional
        // no está implementada de manera estándar. Se devuelve null por defecto.
        LOGGER.warn("La comunicación bidireccional no está implementada para este conector");
        return null;
    }

    public Boolean isCashDrawerOpen() {
        LOGGER.info("Consultando estado del cajón monedero (modo bidireccional)");

        // Verificamos primero si la impresora está conectada
        if (!isConnected()) {
            try {
                connect();
            } catch (IOException e) {
                LOGGER.error("No se pudo conectar con la impresora", e);
                return null;
            }
        }

        try {
            // 1. Preparamos el comando de consulta de estado
            byte[] statusCommand = new byte[3];
            statusCommand[0] = 16;  // DLE
            statusCommand[1] = 4;   // EOT
            statusCommand[2] = 1;   // n=1 (estado de impresora)

            // 2. Limpiamos cualquier dato en el buffer de entrada
            InputStream inputStream = getInputStream();
            if (inputStream == null) {
                LOGGER.warn("No hay soporte para comunicación bidireccional");
                return null;
            }

            int available = inputStream.available();
            if (available > 0) {
                inputStream.skip(available);
            }

            // 3. Enviamos el comando de consulta
            OutputStream outputStream = getOutputStream();
            outputStream.write(statusCommand);
            outputStream.flush();

            // 4. Esperamos respuesta (máximo 1 segundo)
            long startTime = System.currentTimeMillis();
            while (inputStream.available() == 0) {
                if (System.currentTimeMillis() - startTime > 1000) {
                    LOGGER.warn("Timeout esperando respuesta de la impresora");
                    return null;
                }
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }

            // 5. Leemos la respuesta
            int response = inputStream.read();
            LOGGER.debug("Respuesta de estado recibida: " + response);

            // 6. Interpretamos la respuesta
            // Según el protocolo ESC/POS, el bit 2 (valor 4) indica si el cajón está abierto
            // 1 = abierto, 0 = cerrado
            if (response != -1) {
                // Comprobamos el bit 2
                boolean drawerOpen = ((response & 0x04) != 0);
                LOGGER.info("Estado del cajón según la impresora: " + (drawerOpen ? "ABIERTO" : "CERRADO"));
                return drawerOpen;
            } else {
                LOGGER.warn("No se recibió respuesta válida de la impresora");
                return null;
            }
        } catch (IOException e) {
            LOGGER.error("Error al comunicarse con la impresora", e);
            return null;
        }
    }
}