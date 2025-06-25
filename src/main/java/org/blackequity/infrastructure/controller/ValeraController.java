package org.blackequity.infrastructure.controller;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.blackequity.application.service.ValeraService;
import org.blackequity.domain.dto.Valera;
import org.blackequity.infrastructure.dto.request.CreateValeraRequest;
import org.blackequity.infrastructure.dto.request.UseMealRequest;
import org.blackequity.infrastructure.dto.respose.CustomerValerasResponse;
import org.blackequity.infrastructure.dto.respose.ValeraStatsResponse;
import org.blackequity.shared.dto.ValeraDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

@Path("/api/valeras")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ValeraController {

    private static final Logger logger = LoggerFactory.getLogger(ValeraController.class);

    @Inject
    ValeraService service;

    @POST
    public Response createValera(@Valid CreateValeraRequest request) {
        logger.info("POST /api/valeras - Creando valera para: {}", request.getCustomerName());

        try {
            Valera valera = service.createValera(request);
            logger.info("Valera creada exitosamente: {}", valera.getCode());
            return Response.status(Response.Status.CREATED).entity(valera).build();
        } catch (IllegalArgumentException e) {
            logger.warn("⚠Datos inválidos para crear valera: {}", e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", e.getMessage())).build();
        } catch (Exception e) {
            logger.error("Error inesperado creando valera", e);
            return Response.serverError().build();
        }
    }

    @POST
    @Path("/use-meal")
    public Response useMeal(@Valid UseMealRequest request) {
        logger.info("🍽️ POST /api/valeras/use-meal - Usando comida de valera: {}", request.getValeraCode());

        try {
            Valera valera = service.useMeal(request);
            logger.info("Comida registrada exitosamente. Quedan: {} comidas", valera.getRemainingMeals());
            return Response.ok(valera).build();
        } catch (IllegalArgumentException e) {
            logger.warn("Error usando comida: {}", e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", e.getMessage())).build();
        } catch (IllegalStateException e) {
            logger.warn("Estado inválido para usar comida: {}", e.getMessage());
            return Response.status(Response.Status.CONFLICT)
                    .entity(Map.of("error", e.getMessage())).build();
        } catch (Exception e) {
            logger.error("Error inesperado usando comida", e);
            return Response.serverError().build();
        }
    }

    @GET
    @Path("/code/{code}")
    public Response getValeraByCode(@PathParam("code") String code) {
        logger.info("🔍 GET /api/valeras/code/{} - Consultando valera", code);

        try {
            ValeraDto valera = service.getValeraByCode(code);
            logger.info("Valera encontrada: {}", code);
            return Response.ok(valera).build();
        } catch (IllegalArgumentException e) {
            logger.warn("Valera no encontrada: {}", code);
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("error", e.getMessage())).build();
        } catch (Exception e) {
            logger.error("Error consultando valera: {}", code, e);
            return Response.serverError().build();
        }
    }

    @GET
    @Path("/customer/{customerDocument}")
    public Response getCustomerValeras(@PathParam("customerDocument") String customerDocument) {
        logger.info("👤 GET /api/valeras/customer/{} - Consultando valeras del cliente", customerDocument);

        try {
            CustomerValerasResponse response = service.getCustomerValeras(customerDocument);
            logger.info("{} valeras encontradas para cliente: {}", response.getTotalValeras(), customerDocument);
            return Response.ok(response).build();
        } catch (IllegalArgumentException e) {
            logger.warn("Error consultando cliente: {}", e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", e.getMessage())).build();
        } catch (Exception e) {
            logger.error("Error consultando valeras del cliente: {}", customerDocument, e);
            return Response.serverError().build();
        }
    }

    @GET
    @Path("/active")
    public Response getActiveValeras() {
        logger.info("📋 GET /api/valeras/active - Consultando valeras activas");

        try {
            List<ValeraDto> valeras = service.getActiveValeras();
            logger.info("{} valeras activas encontradas", valeras.size());
            return Response.ok(valeras).build();
        } catch (Exception e) {
            logger.error("Error consultando valeras activas", e);
            return Response.serverError().build();
        }
    }

    @GET
    @Path("/expiring")
    public Response getExpiringValeras(@QueryParam("days") @DefaultValue("7") int days) {
        logger.info("GET /api/valeras/expiring?days={} - Consultando valeras por vencer", days);

        try {
            List<ValeraDto> valeras = service.getExpiringValeras(days);
            logger.info("{} valeras por vencer en {} días", valeras.size(), days);
            return Response.ok(valeras).build();
        } catch (IllegalArgumentException e) {
            logger.warn("Parámetro de días inválido: {}", e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", e.getMessage())).build();
        } catch (Exception e) {
            logger.error("Error consultando valeras por vencer", e);
            return Response.serverError().build();
        }
    }

    @POST
    @Path("/expire-old")
    public Response processExpiredValeras() {
        logger.info("POST /api/valeras/expire-old - Procesando expiración automática");

        try {
            service.processExpiredValeras();
            logger.info("Proceso de expiración completado");
            return Response.ok(Map.of("message", "Proceso de expiración completado")).build();
        } catch (Exception e) {
            logger.error("Error procesando expiración automática", e);
            return Response.serverError().build();
        }
    }

    @PUT
    @Path("/{id}/suspend")
    public Response suspendValera(@PathParam("id") String id, Map<String, String> request) {
        String reason = request.get("reason");
        logger.info("PUT /api/valeras/{}/suspend - Suspendiendo valera", id);

        try {
            Valera valera = service.suspendValera(id, reason);
            logger.info("Valera {} suspendida", id);
            return Response.ok(valera).build();
        } catch (IllegalArgumentException e) {
            logger.warn("Error suspendiendo valera: {}", e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", e.getMessage())).build();
        } catch (Exception e) {
            logger.error("Error inesperado suspendiendo valera: {}", id, e);
            return Response.serverError().build();
        }
    }

    @PUT
    @Path("/{id}/reactivate")
    public Response reactivateValera(@PathParam("id") String id) {
        logger.info("PUT /api/valeras/{}/reactivate - Reactivando valera", id);

        try {
            Valera valera = service.reactivateValera(id);
            logger.info("Valera {} reactivada", id);
            return Response.ok(valera).build();
        } catch (IllegalArgumentException e) {
            logger.warn("Error reactivando valera: {}", e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", e.getMessage())).build();
        } catch (Exception e) {
            logger.error("Error inesperado reactivando valera: {}", id, e);
            return Response.serverError().build();
        }
    }

    @PUT
    @Path("/{id}/cancel")
    public Response cancelValera(@PathParam("id") String id, Map<String, String> request) {
        String reason = request.get("reason");
        logger.info("PUT /api/valeras/{}/cancel - Cancelando valera", id);

        try {
            Valera valera = service.cancelValera(id, reason);
            logger.info("Valera {} cancelada", id);
            return Response.ok(valera).build();
        } catch (IllegalArgumentException e) {
            logger.warn("Error cancelando valera: {}", e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", e.getMessage())).build();
        } catch (Exception e) {
            logger.error("Error inesperado cancelando valera: {}", id, e);
            return Response.serverError().build();
        }
    }

    @GET
    @Path("/statistics")
    public Response getStatistics() {
        logger.info("GET /api/valeras/statistics - Consultando estadísticas");

        try {
            ValeraStatsResponse stats = service.getStatistics();
            logger.info("Estadísticas generadas exitosamente");
            return Response.ok(stats).build();
        } catch (Exception e) {
            logger.error("Error generando estadísticas", e);
            return Response.serverError().build();
        }
    }

    @GET
    @Path("/search")
    public Response searchValeras(@QueryParam("q") String searchTerm) {
        logger.info("🔍 GET /api/valeras/search?q={} - Buscando valeras", searchTerm);

        try {
            List<ValeraDto> valeras = service.searchValeras(searchTerm);
            logger.info("{} valeras encontradas", valeras.size());
            return Response.ok(valeras).build();
        } catch (IllegalArgumentException e) {
            logger.warn("Término de búsqueda inválido: {}", e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", e.getMessage())).build();
        } catch (Exception e) {
            logger.error("Error buscando valeras", e);
            return Response.serverError().build();
        }
    }
}
