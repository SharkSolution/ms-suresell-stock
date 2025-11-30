package org.blackequity.infrastructure.controller;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.blackequity.application.service.DiscountService;
import org.blackequity.application.service.AdminPasswordException;
import org.blackequity.domain.dto.*;
import org.blackequity.domain.model.DiscountCouponEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

@Path("/api/discounts")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class DiscountController {

    private static final Logger logger = LoggerFactory.getLogger(DiscountController.class);

    @Inject
    DiscountService discountService;

    /**
     * Endpoint para aplicar un cupón de descuento a una orden (previsualización)
     * POST /api/discounts/apply
     */
    @POST
    @Path("/apply")
    public Response applyDiscount(@Valid ApplyDiscountCommand command) {
        logger.info("POST /api/discounts/apply - Aplicando cupón: {}", command.getCode());

        try {
            ApplyDiscountResult result = discountService.applyDiscount(command);

            if (result.getValid()) {
                logger.info("Cupón aplicado exitosamente: {} - Descuento: ${}",
                    command.getCode(), result.getDiscountAmount());
                return Response.ok(result).build();
            } else {
                logger.warn("Cupón no válido: {} - Razón: {}", command.getCode(), result.getMessage());
                // Retornamos 200 pero con valid=false para que el frontend lo maneje
                return Response.ok(result).build();
            }

        } catch (IllegalArgumentException e) {
            logger.warn("Datos inválidos para aplicar descuento: {}", e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(Map.of("error", e.getMessage()))
                .build();

        } catch (Exception e) {
            logger.error("Error inesperado aplicando descuento", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(Map.of("error", "Error interno del servidor"))
                .build();
        }
    }

    /**
     * Endpoint para registrar que una orden usó un cupón
     * POST /api/discounts/link-order
     */
    @POST
    @Path("/link-order")
    public Response linkOrderWithCoupon(@Valid LinkOrderCouponCommand command) {
        logger.info("POST /api/discounts/link-order - Registrando cupón {} para orden {}",
            command.getCode(), command.getOrderId());

        try {
            discountService.linkOrderWithCoupon(command);
            logger.info("Uso de cupón registrado exitosamente para orden {}", command.getOrderId());

            return Response.status(Response.Status.CREATED)
                .entity(Map.of(
                    "success", true,
                    "message", "Cupón registrado exitosamente",
                    "orderId", command.getOrderId(),
                    "discountCode", command.getCode()
                ))
                .build();

        } catch (IllegalArgumentException e) {
            logger.warn("Datos inválidos para registrar cupón: {}", e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(Map.of("error", e.getMessage()))
                .build();

        } catch (IllegalStateException e) {
            logger.warn("Estado inválido para registrar cupón: {}", e.getMessage());
            return Response.status(Response.Status.CONFLICT)
                .entity(Map.of("error", e.getMessage()))
                .build();

        } catch (Exception e) {
            logger.error("Error inesperado registrando uso de cupón", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(Map.of("error", "Error interno del servidor"))
                .build();
        }
    }

    // ========== Endpoints de administración ==========

    /**
     * Obtiene cupones activos y vigentes (público, sin password)
     * GET /api/discounts/active
     */
    @GET
    @Path("/active")
    public Response getActiveCoupons() {
        logger.info("GET /api/discounts/active - Obteniendo cupones activos");

        try {
            var coupons = discountService.getActiveCoupons();
            return Response.ok(coupons).build();

        } catch (Exception e) {
            logger.error("Error obteniendo cupones activos", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(Map.of("error", "Error interno del servidor"))
                .build();
        }
    }

    /**
     * Crea un nuevo cupón (requiere password admin)
     * POST /api/discounts
     */
    @POST
    public Response createCoupon(@Valid CreateCouponRequest request) {
        logger.info("POST /api/discounts - Creando cupón: {}", request.getCode());

        try {
            // Convertir request a entidad
            DiscountCouponEntity coupon = new DiscountCouponEntity();
            coupon.setCode(request.getCode());
            coupon.setName(request.getName());
            coupon.setDescription(request.getDescription());
            coupon.setDiscountType(request.getDiscountType());
            coupon.setDiscountValue(request.getDiscountValue());
            coupon.setAppliesToType(request.getAppliesToType());
            coupon.setAppliesToId(request.getAppliesToId());
            coupon.setValidFrom(request.getValidFrom());
            coupon.setValidTo(request.getValidTo());
            coupon.setValidWeekdays(request.getValidWeekdays());
            coupon.setIsActive(request.getIsActive() != null ? request.getIsActive() : true);

            DiscountCouponEntity created = discountService.createCoupon(request.getAdminPassword(), coupon);

            logger.info("Cupón creado exitosamente: {}", created.getCode());
            return Response.status(Response.Status.CREATED).entity(created).build();

        } catch (AdminPasswordException e) {
            // El mapper se encargará de convertirlo a 403
            throw e;

        } catch (IllegalArgumentException e) {
            logger.warn("Datos inválidos para crear cupón: {}", e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(Map.of("error", e.getMessage()))
                .build();

        } catch (Exception e) {
            logger.error("Error inesperado creando cupón", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(Map.of("error", "Error interno del servidor"))
                .build();
        }
    }

    /**
     * Actualiza un cupón existente (requiere password admin)
     * PUT /api/discounts/{id}
     */
    @PUT
    @Path("/{id}")
    public Response updateCoupon(@PathParam("id") Long id, @Valid UpdateCouponRequest request) {
        logger.info("PUT /api/discounts/{} - Actualizando cupón", id);

        try {
            // Convertir request a entidad
            DiscountCouponEntity updatedData = new DiscountCouponEntity();
            updatedData.setCode(request.getCode());
            updatedData.setName(request.getName());
            updatedData.setDescription(request.getDescription());
            updatedData.setDiscountType(request.getDiscountType());
            updatedData.setDiscountValue(request.getDiscountValue());
            updatedData.setAppliesToType(request.getAppliesToType());
            updatedData.setAppliesToId(request.getAppliesToId());
            updatedData.setValidFrom(request.getValidFrom());
            updatedData.setValidTo(request.getValidTo());
            updatedData.setValidWeekdays(request.getValidWeekdays());
            updatedData.setIsActive(request.getIsActive());

            DiscountCouponEntity updated = discountService.updateCoupon(request.getAdminPassword(), id, updatedData);

            logger.info("Cupón actualizado exitosamente: {}", updated.getCode());
            return Response.ok(updated).build();

        } catch (AdminPasswordException e) {
            throw e;

        } catch (IllegalArgumentException e) {
            logger.warn("Datos inválidos para actualizar cupón: {}", e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(Map.of("error", e.getMessage()))
                .build();

        } catch (Exception e) {
            logger.error("Error inesperado actualizando cupón", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(Map.of("error", "Error interno del servidor"))
                .build();
        }
    }

    /**
     * Desactiva un cupón (requiere password admin)
     * PATCH /api/discounts/{id}/deactivate
     */
    @PATCH
    @Path("/{id}/deactivate")
    public Response deactivateCoupon(@PathParam("id") Long id, @Valid AdminActionRequest request) {
        logger.info("PATCH /api/discounts/{}/deactivate - Desactivando cupón", id);

        try {
            DiscountCouponEntity deactivated = discountService.deactivateCoupon(request.getAdminPassword(), id);

            logger.info("Cupón desactivado exitosamente: {}", deactivated.getCode());
            return Response.ok()
                .entity(Map.of(
                    "success", true,
                    "message", "Cupón desactivado exitosamente",
                    "coupon", deactivated
                ))
                .build();

        } catch (AdminPasswordException e) {
            throw e;

        } catch (IllegalArgumentException e) {
            logger.warn("Error desactivando cupón: {}", e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(Map.of("error", e.getMessage()))
                .build();

        } catch (Exception e) {
            logger.error("Error inesperado desactivando cupón", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(Map.of("error", "Error interno del servidor"))
                .build();
        }
    }

    /**
     * Lista todos los cupones con filtros (requiere password admin)
     * GET /api/discounts?adminPassword=xxx&status=active|inactive|expired|all
     */
    @GET
    public Response listAllCoupons(
        @QueryParam("adminPassword") String adminPassword,
        @QueryParam("status") @DefaultValue("all") String status) {

        logger.info("GET /api/discounts - Listando cupones con filtro: {}", status);

        try {
            var coupons = discountService.listAllCoupons(adminPassword, status);

            return Response.ok(coupons).build();

        } catch (AdminPasswordException e) {
            throw e;

        } catch (Exception e) {
            logger.error("Error listando cupones", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(Map.of("error", "Error interno del servidor"))
                .build();
        }
    }
}
