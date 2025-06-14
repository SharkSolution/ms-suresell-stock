package org.blackequity.application.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.blackequity.application.usecase.ManageMealPreparationUseCase;
import org.blackequity.domain.dto.MealPreparation;
import org.blackequity.infrastructure.dto.request.CreateMealPreparationRequest;
import org.blackequity.infrastructure.dto.request.UpdateMealPreparationRequest;
import org.blackequity.infrastructure.dto.respose.WeeklyMealPlanResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;

@ApplicationScoped
public class MealPreparationService {

    private static final Logger logger = LoggerFactory.getLogger(MealPreparationService.class);

    @Inject
    ManageMealPreparationUseCase useCase;

    public MealPreparation createMealPreparation(CreateMealPreparationRequest request) {
        logger.info("🍽️ Creando nueva preparación para {}", request.getPreparationDate());

        validateCreateRequest(request);
        return useCase.createMealPreparation(request);
    }

    public WeeklyMealPlanResponse getCurrentWeekPlan() {
        logger.debug("Obteniendo plan de preparaciones semana actual");
        return useCase.getCurrentWeekPlan();
    }

    public WeeklyMealPlanResponse getNextWeekPlan() {
        logger.debug("Obteniendo plan de preparaciones semana siguiente");
        return useCase.getNextWeekPlan();
    }

    public WeeklyMealPlanResponse getWeekPlan(LocalDate weekStartDate) {
        logger.debug("Obteniendo plan de preparaciones para semana: {}", weekStartDate);

        if (weekStartDate == null) {
            throw new IllegalArgumentException("Fecha de inicio de semana es requerida");
        }

        return useCase.getWeekPlan(weekStartDate);
    }

    public MealPreparation updateMealPreparation(String id, UpdateMealPreparationRequest request) {
        logger.info("🔄 Actualizando preparación: {}", id);

        validateUpdateRequest(request);
        return useCase.updateMealPreparation(id, request);
    }

    public void startPreparation(String id) {
        logger.info("Iniciando preparación: {}", id);
        useCase.startPreparation(id);
    }

    public void completePreparation(String id) {
        logger.info("Marcando preparación como completada: {}", id);
        useCase.completePreparation(id);
    }

    public void cancelPreparation(String id) {
        logger.info("Cancelando preparación: {}", id);
        useCase.cancelPreparation(id);
    }

    public void deleteMealPreparation(String id) {
        logger.info("Eliminando preparación: {}", id);
        useCase.deleteMealPreparation(id);
    }

    private void validateCreateRequest(CreateMealPreparationRequest request) {
        if (request.getPreparationDate() == null) {
            throw new IllegalArgumentException("Fecha de preparación es requerida");
        }

        if (request.getMainDish() == null || request.getMainDish().trim().isEmpty()) {
            throw new IllegalArgumentException("Plato principal es requerido");
        }

        if (request.getEstimatedPortions() != null && request.getEstimatedPortions() <= 0) {
            throw new IllegalArgumentException("Porciones estimadas debe ser un número positivo");
        }

        // Validar que la fecha no sea muy en el pasado
        if (request.getPreparationDate().isBefore(LocalDate.now().minusDays(7))) {
            throw new IllegalArgumentException("No se puede crear preparaciones para fechas muy pasadas");
        }
    }

    private void validateUpdateRequest(UpdateMealPreparationRequest request) {
        if (request.getMainDish() != null && request.getMainDish().trim().isEmpty()) {
            throw new IllegalArgumentException("Plato principal no puede estar vacío");
        }

        if (request.getEstimatedPortions() != null && request.getEstimatedPortions() <= 0) {
            throw new IllegalArgumentException("Porciones estimadas debe ser un número positivo");
        }
    }
}
