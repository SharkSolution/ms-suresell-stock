package org.blackequity.printer.api;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.blackequity.printer.domain.PrintResponse;
import org.blackequity.printer.domain.PrinterStatus;
import org.blackequity.printer.domain.TicketRequest;
import org.blackequity.printer.service.PrinterService;
import org.jboss.logging.Logger;


@Path("/api/print")
@ApplicationScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PrinterResource {

    private static final Logger LOGGER = Logger.getLogger(PrinterResource.class);

    @Inject
    PrinterService printerService;

    /**
     * Imprime un ticket de venta
     */
    @POST
    @Path("/ticket")
    public Response printTicket(TicketRequest ticketRequest) {
        try {
            printerService.printTicket(ticketRequest);
            return Response.ok(new PrintResponse(true, "Ticket impreso correctamente")).build();
        } catch (Exception e) {
            LOGGER.error("Error al imprimir ticket", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new PrintResponse(false, "Error al imprimir: " + e.getMessage()))
                    .build();
        }
    }

    /**
     * Imprime un ticket de test para verificar la configuración
     */
    @GET
    @Path("/test")
    public Response printTestTicket() {
        try {
            printerService.printTestTicket();
            return Response.ok(new PrintResponse(true, "Ticket de prueba impreso correctamente")).build();
        } catch (Exception e) {
            LOGGER.error("Error al imprimir ticket de prueba", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new PrintResponse(false, "Error al imprimir: " + e.getMessage()))
                    .build();
        }
    }

    /**
     * Verifica el estado de la impresora
     */
    @GET
    @Path("/status")
    public Response getPrinterStatus() {
        try {
            PrinterStatus status = printerService.getPrinterStatus();
            return Response.ok(status).build();
        } catch (Exception e) {
            LOGGER.error("Error al obtener estado de la impresora", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new PrintResponse(false, "Error: " + e.getMessage()))
                    .build();
        }
    }

    /**
     * Abre el cajón monedero
     */
    @POST
    @Path("/drawer/open")
    public Response openCashDrawer() {
        try {
            printerService.openCashDrawer();
            return Response.ok(new PrintResponse(true, "Cajón monedero abierto correctamente")).build();
        } catch (Exception e) {
            LOGGER.error("Error al abrir cajón monedero", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new PrintResponse(false, "Error al abrir cajón: " + e.getMessage()))
                    .build();
        }
    }

}