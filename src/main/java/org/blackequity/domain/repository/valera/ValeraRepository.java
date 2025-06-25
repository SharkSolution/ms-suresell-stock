package org.blackequity.domain.repository.valera;

import org.blackequity.domain.dto.Valera;
import org.blackequity.domain.enums.ValeraStatus;
import org.blackequity.domain.enums.ValeraType;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ValeraRepository {
    List<Valera> findAllValeras();
    Optional<Valera> findById(String id);
    Optional<Valera> findByCode(String code);
    List<Valera> findByCustomerDocument(String customerDocument);
    List<Valera> findByStatus(ValeraStatus status);
    List<Valera> findByType(ValeraType type);
    List<Valera> findActiveValeras();
    List<Valera> findExpiringValeras(int daysAhead);
    List<Valera> findExpiredValeras();
    List<Valera> findByDateRange(LocalDate startDate, LocalDate endDate);
    Valera create(Valera valera);
    Valera update(Valera valera);
    void deleteById(String id);
    boolean existsByCode(String code);
    long countByStatus(ValeraStatus status);
    long countByCustomerDocument(String customerDocument);
}
