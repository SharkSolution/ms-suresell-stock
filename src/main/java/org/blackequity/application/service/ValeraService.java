package org.blackequity.application.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.blackequity.application.usecase.ManageValeraUseCase;
import org.blackequity.domain.dto.Valera;
import org.blackequity.domain.enums.ValeraStatus;
import org.blackequity.domain.enums.ValeraType;
import org.blackequity.infrastructure.dto.request.CreateValeraRequest;
import org.blackequity.infrastructure.dto.request.UseMealRequest;
import org.blackequity.infrastructure.dto.respose.CustomerValerasResponse;
import org.blackequity.infrastructure.dto.respose.ValeraStatsResponse;
import org.blackequity.shared.dto.ValeraDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.List;

@ApplicationScoped
public class ValeraService {

    private static final Logger logger = LoggerFactory.getLogger(ValeraService.class);

    @Inject
    ManageValeraUseCase useCase;

    public Valera createValera(CreateValeraRequest request) {
        logger.info("Creando nueva valera para: {}", request.getCustomerName());

        validateCreateRequest(request);
        return useCase.createValera(request);
    }

    public Valera useMeal(UseMealRequest request) {
        logger.info("Registrando uso de comida para valera: {}", request.getValeraCode());

        validateUseMealRequest(request);
        return useCase.useMeal(request);
    }

    public ValeraDto getValeraByCode(String code) {
        logger.debug("Consultando valera por código: {}", code);

        if (code == null || code.trim().isEmpty()) {
            throw new IllegalArgumentException("Código de valera es requerido");
        }

        return useCase.getValeraByCode(code);
    }

    public CustomerValerasResponse getCustomerValeras(String customerDocument) {
        logger.debug("Consultando valeras del cliente: {}", customerDocument);

        if (customerDocument == null || customerDocument.trim().isEmpty()) {
            throw new IllegalArgumentException("Documento del cliente es requerido");
        }

        return useCase.getCustomerValeras(customerDocument);
    }

    public List<ValeraDto> getActiveValeras() {
        logger.debug("Obteniendo valeras activas");
        return useCase.getActiveValeras();
    }

    public List<ValeraDto> getExpiringValeras(int daysAhead) {
        logger.debug("Obteniendo valeras que vencen en {} días", daysAhead);

        if (daysAhead < 1 || daysAhead > 90) {
            throw new IllegalArgumentException("Los días deben estar entre 1 y 90");
        }

        return useCase.getExpiringValeras(daysAhead);
    }

    public void processExpiredValeras() {
        logger.info("Procesando expiración automática de valeras");
        useCase.expireOldValeras();
    }

    public Valera suspendValera(String valeraId, String reason) {
        logger.info("Suspendiendo valera: {}", valeraId);

        if (reason == null || reason.trim().isEmpty()) {
            throw new IllegalArgumentException("Razón de suspensión es requerida");
        }

        return useCase.suspendValera(valeraId, reason);
    }

    public Valera reactivateValera(String valeraId) {
        logger.info("▶️ Reactivando valera: {}", valeraId);
        return useCase.reactivateValera(valeraId);
    }

    public Valera cancelValera(String valeraId, String reason) {
        logger.info("Cancelando valera: {}", valeraId);

        if (reason == null || reason.trim().isEmpty()) {
            throw new IllegalArgumentException("Razón de cancelación es requerida");
        }

        return useCase.cancelValera(valeraId, reason);
    }

    public ValeraStatsResponse getStatistics() {
        logger.debug("Obteniendo estadísticas de valeras");
        return useCase.getValeraStatistics();
    }

    public List<ValeraDto> searchValeras(String searchTerm) {
        logger.debug("🔍 Buscando valeras: {}", searchTerm);

        if (searchTerm == null || searchTerm.trim().length() < 2) {
            throw new IllegalArgumentException("Término de búsqueda debe tener al menos 2 caracteres");
        }

        return useCase.searchValeras(searchTerm);
    }

    public List<ValeraDto> getValerasByDateRange(LocalDate startDate, LocalDate endDate) {
        logger.debug("Consultando valeras por rango de fechas: {} - {}", startDate, endDate);

        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Fechas de inicio y fin son requeridas");
        }

        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Fecha de inicio no puede ser mayor a fecha de fin");
        }

        return useCase.getValerasByDateRange(startDate, endDate);
    }

    public List<ValeraDto> getValerasByType(String typeName) {
        logger.debug("Consultando valeras de tipo: {}", typeName);

        try {
            ValeraType type = ValeraType.valueOf(typeName.toUpperCase());
            return useCase.getValerasByType(type);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Tipo de valera inválido: " + typeName);
        }
    }

    public List<ValeraDto> getValerasByStatus(String statusName) {
        logger.debug("Consultando valeras con estado: {}", statusName);

        try {
            ValeraStatus status = ValeraStatus.valueOf(statusName.toUpperCase());
            return useCase.getValerasByStatus(status);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Estado de valera inválido: " + statusName);
        }
    }

    public void deleteValera(String valeraId) {
        logger.info("Eliminando valera: {}", valeraId);

        if (valeraId == null || valeraId.trim().isEmpty()) {
            throw new IllegalArgumentException("ID de valera es requerido");
        }

        useCase.deleteValera(valeraId);
    }

    public boolean isValidValeraCode(String code) {
        logger.debug("Validando código de valera: {}", code);

        if (code == null || code.trim().isEmpty()) {
            return false;
        }

        return useCase.validateValeraCode(code);
    }

    public long getCustomerValeraCount(String customerDocument) {
        logger.debug("Contando valeras del cliente: {}", customerDocument);

        if (customerDocument == null || customerDocument.trim().isEmpty()) {
            throw new IllegalArgumentException("Documento del cliente es requerido");
        }

        return useCase.getCustomerValeraCount(customerDocument);
    }

    private void validateCreateRequest(CreateValeraRequest request) {
        if (request.getCustomerName() == null || request.getCustomerName().trim().isEmpty()) {
            throw new IllegalArgumentException("Nombre del cliente es requerido");
        }

        if (request.getCustomerDocument() == null || request.getCustomerDocument().trim().isEmpty()) {
            throw new IllegalArgumentException("Documento del cliente es requerido");
        }

        if (request.getTotalMeals() == null || request.getTotalMeals() <= 0) {
            throw new IllegalArgumentException("Total de comidas debe ser positivo");
        }

        if (request.getUnitPrice() == null || request.getUnitPrice().compareTo(java.math.BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Precio unitario debe ser positivo");
        }

        if (request.getValidityDays() == null || request.getValidityDays() <= 0) {
            throw new IllegalArgumentException("Días de validez debe ser positivo");
        }

        // Validar que el tipo de valera sea válido
        try {
            ValeraType.valueOf(request.getType().toUpperCase());
        } catch (Exception e) {
            throw new IllegalArgumentException("Tipo de valera inválido: " + request.getType());
        }
    }

    private void validateUseMealRequest(UseMealRequest request) {
        if (request.getValeraCode() == null || request.getValeraCode().trim().isEmpty()) {
            throw new IllegalArgumentException("Código de valera es requerido");
        }
    }
}