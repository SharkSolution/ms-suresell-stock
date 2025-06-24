package org.blackequity.infrastructure.controller;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.blackequity.application.service.AccountReceivableService;
import org.blackequity.domain.dto.AccountReceivable;
import org.blackequity.shared.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Path("/api/accounts-receivable")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AccountReceivableController {

    private static final Logger logger = LoggerFactory.getLogger(AccountReceivableController.class);

    @Inject
    AccountReceivableService service;

    @POST
    public Response createAccount(@Valid CreateAccountRequest request) {
        logger.info("POST /api/accounts-receivable - Creando cuenta para: {}", request.getCustomerName());

        try {
            AccountReceivable account = service.createAccount(request);
            logger.info("Cuenta creada exitosamente para: {}", account.getCustomerName());
            return Response.status(Response.Status.CREATED).entity(account).build();
        } catch (IllegalArgumentException e) {
            logger.warn("Datos inválidos para crear cuenta: {}", e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", e.getMessage())).build();
        } catch (Exception e) {
            logger.error("Error inesperado creando cuenta", e);
            return Response.serverError().build();
        }
    }

    @POST
    @Path("/add-debt")
    public Response addDebt(@Valid AddDebtRequest request) {
        logger.info("POST /api/accounts-receivable/add-debt - Agregando deuda de ${} para: {}",
                request.getAmount(), request.getCustomerDocument());

        try {
            AccountReceivable account = service.addDebt(request);
            logger.info("Deuda agregada exitosamente. Nueva deuda total: ${}", account.getTotalDebt());
            return Response.ok(account).build();
        } catch (IllegalArgumentException e) {
            logger.warn("Error agregando deuda: {}", e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", e.getMessage())).build();
        } catch (IllegalStateException e) {
            logger.warn("Estado inválido para agregar deuda: {}", e.getMessage());
            return Response.status(Response.Status.CONFLICT)
                    .entity(Map.of("error", e.getMessage())).build();
        } catch (Exception e) {
            logger.error("Error inesperado agregando deuda", e);
            return Response.serverError().build();
        }
    }

    @POST
    @Path("/make-payment")
    public Response makePayment(@Valid MakePaymentRequest request) {
        logger.info("POST /api/accounts-receivable/make-payment - Procesando pago de ${} para: {}",
                request.getAmount(), request.getCustomerDocument());

        try {
            AccountReceivable account = service.makePayment(request);
            logger.info("Pago procesado exitosamente. Deuda restante: ${}", account.getTotalDebt());
            return Response.ok(account).build();
        } catch (IllegalArgumentException e) {
            logger.warn("Error procesando pago: {}", e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", e.getMessage())).build();
        } catch (IllegalStateException e) {
            logger.warn("Estado inválido para procesar pago: {}", e.getMessage());
            return Response.status(Response.Status.CONFLICT)
                    .entity(Map.of("error", e.getMessage())).build();
        } catch (Exception e) {
            logger.error("Error inesperado procesando pago", e);
            return Response.serverError().build();
        }
    }

    @GET
    @Path("/customer/{customerDocument}")
    public Response getAccountByDocument(@PathParam("customerDocument") String customerDocument) {
        logger.info("🔍 GET /api/accounts-receivable/customer/{} - Consultando cuenta", customerDocument);

        try {
            AccountReceivableDto account = service.getAccountByDocument(customerDocument);
            logger.info("Cuenta encontrada: {}", customerDocument);
            return Response.ok(account).build();
        } catch (IllegalArgumentException e) {
            logger.warn("Cliente no encontrado: {}", customerDocument);
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("error", e.getMessage())).build();
        } catch (Exception e) {
            logger.error("Error consultando cuenta: {}", customerDocument, e);
            return Response.serverError().build();
        }
    }

//    @GET
//    @Path("/customer/{customerDocument}/details")
//    public Response getCustomerAccountDetails(@PathParam("customerDocument") String customerDocument) {
//        logger.info("📋 GET /api/accounts-receivable/customer/{}/details - Detalles completos", customerDocument);
//
//        try {
//            CustomerAccountResponse response = service.getCustomerAccountDetails(customerDocument);
//            logger.info("Detalles obtenidos para: {}", customerDocument);
//            return Response.ok(response).build();
//        } catch (IllegalArgumentException e) {
//            logger.warn("Cliente no encontrado: {}", customerDocument);
//            return Response.status(Response.Status.NOT_FOUND)
//                    .entity(Map.of("error", e.getMessage())).build();
//        } catch (Exception e) {
//            logger.error("Error obteniendo detalles: {}", customerDocument, e);
//            return Response.serverError().build();
//        }
//    }

    @GET
    @Path("/with-debt")
    public Response getAccountsWithDebt() {
        logger.info("📋 GET /api/accounts-receivable/with-debt - Consultando cuentas con deuda");

        try {
            List<AccountReceivableDto> accounts = service.getAccountsWithDebt();
            logger.info("{} cuentas con deuda encontradas", accounts.size());
            return Response.ok(accounts).build();
        } catch (Exception e) {
            logger.error("Error consultando cuentas con deuda", e);
            return Response.serverError().build();
        }
    }

    @GET
    @Path("/overdue")
    public Response getOverdueAccounts(@QueryParam("days") @DefaultValue("30") int days) {
        logger.info("⏰ GET /api/accounts-receivable/overdue?days={} - Cuentas vencidas", days);

        try {
            List<AccountReceivableDto> accounts = service.getOverdueAccounts(days);
            logger.info("{} cuentas vencidas encontradas", accounts.size());
            return Response.ok(accounts).build();
        } catch (IllegalArgumentException e) {
            logger.warn("Parámetro de días inválido: {}", e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", e.getMessage())).build();
        } catch (Exception e) {
            logger.error("Error consultando cuentas vencidas", e);
            return Response.serverError().build();
        }
    }

    @GET
    @Path("/status/{status}")
    public Response getAccountsByStatus(@PathParam("status") String status) {
        logger.info("GET /api/accounts-receivable/status/{} - Cuentas por estado", status);

        try {
            List<AccountReceivableDto> accounts = service.getAccountsByStatus(status);
            logger.info("{} cuentas con estado {} encontradas", accounts.size(), status);
            return Response.ok(accounts).build();
        } catch (IllegalArgumentException e) {
            logger.warn("Estado inválido: {}", e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", e.getMessage())).build();
        } catch (Exception e) {
            logger.error("Error consultando cuentas vencidas", e);
            return Response.serverError().build();
        }
    }

    @PUT
    @Path("/customer/{customerDocument}/close")
    public Response closeAccount(@PathParam("customerDocument") String customerDocument,
                                 Map<String, String> request) {
        String reason = request.get("reason");
        logger.info("PUT /api/accounts-receivable/customer/{}/close - Cerrando cuenta", customerDocument);

        try {
            AccountReceivable account = service.closeAccount(customerDocument, reason);
            logger.info("Cuenta {} cerrada", customerDocument);
            return Response.ok(account).build();
        } catch (IllegalArgumentException e) {
            logger.warn("Error cerrando cuenta: {}", e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", e.getMessage())).build();
        } catch (IllegalStateException e) {
            logger.warn("Estado inválido para cerrar cuenta: {}", e.getMessage());
            return Response.status(Response.Status.CONFLICT)
                    .entity(Map.of("error", e.getMessage())).build();
        } catch (Exception e) {
            logger.error("Error inesperado cerrando cuenta: {}", customerDocument, e);
            return Response.serverError().build();
        }
    }

    @PUT
    @Path("/customer/{customerDocument}/credit-limit")
    public Response updateCreditLimit(@PathParam("customerDocument") String customerDocument,
                                      Map<String, BigDecimal> request) {
        BigDecimal newLimit = request.get("creditLimit");
        logger.info("PUT /api/accounts-receivable/customer/{}/credit-limit - Nuevo límite: ${}",
                customerDocument, newLimit);

        try {
            AccountReceivable account = service.updateCreditLimit(customerDocument, newLimit);
            logger.info("Límite de crédito actualizado para: {}", customerDocument);
            return Response.ok(account).build();
        } catch (IllegalArgumentException e) {
            logger.warn("Error actualizando límite: {}", e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", e.getMessage())).build();
        } catch (Exception e) {
            logger.error("Error inesperado actualizando límite: {}", customerDocument, e);
            return Response.serverError().build();
        }
    }

    @GET
    @Path("/customer/{customerDocument}/transactions")
    public Response getTransactionHistory(@PathParam("customerDocument") String customerDocument,
                                          @QueryParam("startDate") String startDateStr,
                                          @QueryParam("endDate") String endDateStr) {
        logger.info("GET /api/accounts-receivable/customer/{}/transactions - Historial", customerDocument);

        try {
            LocalDate startDate = startDateStr != null ? LocalDate.parse(startDateStr) : null;
            LocalDate endDate = endDateStr != null ? LocalDate.parse(endDateStr) : null;

            List<DebtTransactionDto> transactions = service.getTransactionHistory(customerDocument, startDate, endDate);
            logger.info("{} transacciones encontradas para: {}", transactions.size(), customerDocument);
            return Response.ok(transactions).build();
        } catch (IllegalArgumentException e) {
            logger.warn("Error en historial de transacciones: {}", e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", e.getMessage())).build();
        } catch (Exception e) {
            logger.error("Error obteniendo historial: {}", customerDocument, e);
            return Response.serverError().build();
        }
    }

    @GET
    @Path("/customer/{customerDocument}/debt-amount")
    public Response getCustomerDebt(@PathParam("customerDocument") String customerDocument) {
        logger.info("GET /api/accounts-receivable/customer/{}/debt-amount - Consulta de deuda", customerDocument);

        try {
            BigDecimal debtAmount = service.getCustomerDebt(customerDocument);
            logger.info("Deuda consultada para: {} = ${}", customerDocument, debtAmount);
            return Response.ok(Map.of(
                    "customerDocument", customerDocument,
                    "debtAmount", debtAmount
            )).build();
        } catch (Exception e) {
            logger.error("Error consultando deuda: {}", customerDocument, e);
            return Response.serverError().build();
        }
    }

    @GET
    @Path("/statistics")
    public Response getStatistics() {
        logger.info("GET /api/accounts-receivable/statistics - Estadísticas generales");

        try {
            AccountStatsResponse stats = service.getStatistics();
            logger.info("Estadísticas generadas exitosamente");
            return Response.ok(stats).build();
        } catch (Exception e) {
            logger.error("Error generando estadísticas", e);
            return Response.serverError().build();
        }
    }

}