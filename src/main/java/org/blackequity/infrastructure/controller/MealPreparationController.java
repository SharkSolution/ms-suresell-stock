package org.blackequity.infrastructure.controller;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.blackequity.application.service.MealPreparationService;
import org.blackequity.domain.dto.MealPreparation;
import org.blackequity.infrastructure.dto.request.CreateMealPreparationRequest;
import org.blackequity.infrastructure.dto.request.UpdateMealPreparationRequest;
import org.blackequity.infrastructure.dto.respose.WeeklyMealPlanResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.Map;


@Path("/api/meal-preparations")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class MealPreparationController {

    private static final Logger logger = LoggerFactory.getLogger(MealPreparationController.class);

    @Inject
    MealPreparationService service;

    @POST
    public Response createMealPreparation(@Valid CreateMealPreparationRequest request) {
        logger.info("🍽️ POST /api/meal-preparations - Creando preparación para {}", request.getPreparationDate());

        try {
            MealPreparation meal = service.createMealPreparation(request);
            logger.info("Preparación creada exitosamente: {}", meal.getId());
            return Response.status(Response.Status.CREATED).entity(meal).build();
        } catch (IllegalArgumentException e) {
            logger.warn("⚠Datos inválidos para crear preparación: {}", e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", e.getMessage())).build();
        } catch (Exception e) {
            logger.error("Error inesperado creando preparación", e);
            return Response.serverError().build();
        }
    }

    @GET
    @Path("/week/{offset}")
    public Response getWeekPlan(@PathParam("offset") int offset) {
        logger.info("GET /api/meal-preparations/week/{} - Obteniendo plan semana offset", offset);

        try {
            WeeklyMealPlanResponse plan = service.getAllWeekPlan(offset);
            logger.info("Plan semana offset {} obtenido: {} preparaciones", offset, plan.getTotalMeals());
            return Response.ok(plan).build();
        } catch (Exception e) {
            logger.error("Error obteniendo plan semana offset: {}", offset, e);
            return Response.serverError().build();
        }
    }

    @GET
    @Path("/week/{date}")
    public Response getWeekPlanByDate(@PathParam("date") String dateStr) {
        logger.info("GET /api/meal-preparations/week/{} - Obteniendo plan para fecha", dateStr);

        try {
            LocalDate weekStartDate = LocalDate.parse(dateStr);
            WeeklyMealPlanResponse plan = service.getWeekPlan(weekStartDate);
            logger.info("Plan semana {} obtenido: {} preparaciones", dateStr, plan.getTotalMeals());
            return Response.ok(plan).build();
        } catch (Exception e) {
            logger.error("Error obteniendo plan para semana: {}", dateStr, e);
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", "Fecha inválida: " + dateStr)).build();
        }
    }

    @PUT
    @Path("/{id}")
    public Response updateMealPreparation(@PathParam("id") String id,
                                          @Valid UpdateMealPreparationRequest request) {
        logger.info("🔄 PUT /api/meal-preparations/{} - Actualizando preparación", id);

        try {
            MealPreparation meal = service.updateMealPreparation(id, request);
            logger.info("Preparación {} actualizada exitosamente", id);
            return Response.ok(meal).build();
        } catch (IllegalArgumentException e) {
            logger.warn("⚠Error actualizando preparación {}: {}", id, e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", e.getMessage())).build();
        } catch (Exception e) {
            logger.error("Error inesperado actualizando preparación: {}", id, e);
            return Response.serverError().build();
        }
    }

    @PUT
    @Path("/{id}/start")
    public Response startPreparation(@PathParam("id") String id) {
        logger.info("PUT /api/meal-preparations/{}/start - Iniciando preparación", id);

        try {
            service.startPreparation(id);
            logger.info("Preparación {} iniciada exitosamente", id);
            return Response.ok(Map.of("message", "Preparación iniciada")).build();
        } catch (IllegalArgumentException e) {
            logger.warn("Error iniciando preparación {}: {}", id, e.getMessage());
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("error", e.getMessage())).build();
        } catch (Exception e) {
            logger.error("Error inesperado iniciando preparación: {}", id, e);
            return Response.serverError().build();
        }
    }

    @PUT
    @Path("/{id}/complete")
    public Response completePreparation(@PathParam("id") String id) {
        logger.info("PUT /api/meal-preparations/{}/complete - Completando preparación", id);

        try {
            service.completePreparation(id);
            logger.info("Preparación {} completada exitosamente", id);
            return Response.ok(Map.of("message", "Preparación completada")).build();
        } catch (IllegalArgumentException e) {
            logger.warn("Error completando preparación {}: {}", id, e.getMessage());
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("error", e.getMessage())).build();
        } catch (Exception e) {
            logger.error("Error inesperado completando preparación: {}", id, e);
            return Response.serverError().build();
        }
    }

    @PUT
    @Path("/{id}/cancel")
    public Response cancelPreparation(@PathParam("id") String id) {
        logger.info("PUT /api/meal-preparations/{}/cancel - Cancelando preparación", id);

        try {
            service.cancelPreparation(id);
            logger.info("Preparación {} cancelada exitosamente", id);
            return Response.ok(Map.of("message", "Preparación cancelada")).build();
        } catch (IllegalArgumentException e) {
            logger.warn("⚠Error cancelando preparación {}: {}", id, e.getMessage());
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("error", e.getMessage())).build();
        } catch (Exception e) {
            logger.error("Error inesperado cancelando preparación: {}", id, e);
            return Response.serverError().build();
        }
    }

    @DELETE
    @Path("/{id}")
    public Response deleteMealPreparation(@PathParam("id") String id) {
        logger.info("🗑️ DELETE /api/meal-preparations/{} - Eliminando preparación", id);

        try {
            service.deleteMealPreparation(id);
            logger.info("Preparación {} eliminada exitosamente", id);
            return Response.noContent().build();
        } catch (IllegalArgumentException e) {
            logger.warn("Error eliminando preparación {}: {}", id, e.getMessage());
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("error", e.getMessage())).build();
        } catch (Exception e) {
            logger.error("Error inesperado eliminando preparación: {}", id, e);
            return Response.serverError().build();
        }
    }
}
