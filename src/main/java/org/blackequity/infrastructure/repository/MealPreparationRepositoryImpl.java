package org.blackequity.infrastructure.repository;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.blackequity.application.mapper.MealPreparationEntityMapper;
import org.blackequity.domain.dto.MealPreparation;
import org.blackequity.domain.model.MealPreparationEntity;
import org.blackequity.domain.repository.mealpreparation.MealPreparationRepository;
import org.blackequity.shared.dto.DayOfWeek;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@ApplicationScoped
public class MealPreparationRepositoryImpl implements MealPreparationRepository, PanacheRepository<MealPreparationEntity> {

    private static final Logger logger = LoggerFactory.getLogger(MealPreparationRepositoryImpl.class);

    @Inject
    MealPreparationEntityMapper mapper;

    @Override
    public List<MealPreparation> findAllMealPreparations() {
        logger.debug("Obteniendo todas las preparaciones");
        return listAll().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<MealPreparation> findById(String id) {
        logger.debug("Buscando preparación por ID: {}", id);
        return find("id", id).stream()
                .findFirst()
                .map(mapper::toDomain);
    }

    @Override
    public List<MealPreparation> findByWeek(LocalDate startOfWeek) {
        LocalDate endOfWeek = startOfWeek.plusDays(6);
        logger.debug("Buscando preparaciones para la semana: {} - {}", startOfWeek, endOfWeek);

        return find("preparationDate >= ?1 AND preparationDate <= ?2 ORDER BY preparationDate",
                startOfWeek, endOfWeek)
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<MealPreparation> findByDateRange(LocalDate startDate, LocalDate endDate) {
        logger.debug("Buscando preparaciones en rango: {} - {}", startDate, endDate);

        return find("preparationDate >= ?1 AND preparationDate <= ?2 ORDER BY preparationDate",
                startDate, endDate)
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<MealPreparation> findByDate(LocalDate date) {
        logger.debug("Buscando preparación para fecha: {}", date);

        return find("preparationDate", date).stream()
                .findFirst()
                .map(mapper::toDomain);
    }

    @Override
    public List<MealPreparation> findByDayOfWeek(DayOfWeek dayOfWeek) {
        return find("dayOfWeek", dayOfWeek).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public MealPreparation save(MealPreparation mealPreparation) {
        logger.debug("Guardando preparación: {} para {}",
                mealPreparation.getMainDish(), mealPreparation.getPreparationDate());

        if (mealPreparation.getId() != null) {
            return updateExistingMeal(mealPreparation);
        } else {
            return createNewMeal(mealPreparation);
        }
    }

    private MealPreparation updateExistingMeal(MealPreparation meal) {
        logger.debug("Actualizando preparación existente: {}", meal.getId());

        MealPreparationEntity existingEntity = find("id", meal.getId()).firstResult();
        if (existingEntity == null) {
            throw new IllegalArgumentException("Meal preparation not found: " + meal.getId());
        }

        mapper.updateEntity(existingEntity, meal);
        logger.info("Preparación actualizada: {} para {}", meal.getMainDish(), meal.getPreparationDate());
        return mapper.toDomain(existingEntity);
    }

    private MealPreparation createNewMeal(MealPreparation meal) {
        logger.debug("Creando nueva preparación para: {}", meal.getPreparationDate());
        meal.setId(UUID.randomUUID().toString());
        MealPreparationEntity newEntity = mapper.toEntity(meal);
        persist(newEntity);

        logger.info("Nueva preparación creada: {} para {}", meal.getMainDish(), meal.getPreparationDate());
        return mapper.toDomain(newEntity);
    }

    @Override
    @Transactional
    public void deleteById(String id) {
        logger.debug("Eliminando preparación: {}", id);

        long deletedCount = delete("id", id);
        if (deletedCount == 0) {
            throw new IllegalArgumentException("Meal preparation not found: " + id);
        }

        logger.info("Preparación {} eliminada", id);
    }

    @Override
    public List<MealPreparation> findByWeekOffset(int offset) {
        LocalDate today = LocalDate.now();
        LocalDate targetDate = today.plusWeeks(offset);
        LocalDate startOfWeek = targetDate.with(TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY));

        logger.debug("Buscando preparaciones semana offset {}: {}", offset, startOfWeek);
        return findByWeek(startOfWeek);
    }

}
