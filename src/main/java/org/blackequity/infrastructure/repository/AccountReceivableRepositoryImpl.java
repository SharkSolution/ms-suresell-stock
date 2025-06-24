package org.blackequity.infrastructure.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.blackequity.domain.dto.AccountReceivable;
import org.blackequity.domain.dto.DebtTransaction;
import org.blackequity.domain.enums.AccountStatus;
import org.blackequity.domain.model.AccountReceivableEntity;
import org.blackequity.domain.model.DebtTransactionEntity;
import org.blackequity.domain.repository.AccountReceivable.AccountReceivableRepository;
import org.blackequity.shared.mapper.AccountReceivableEntityMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@ApplicationScoped
public class AccountReceivableRepositoryImpl implements AccountReceivableRepository, PanacheRepository<AccountReceivableEntity> {

    private static final Logger logger = LoggerFactory.getLogger(AccountReceivableRepositoryImpl.class);

    @Inject
    AccountReceivableEntityMapper mapper;

    @Inject
    EntityManager entityManager;

    @Override
    public List<AccountReceivable> findAllAccounts() {
        logger.debug("Obteniendo todas las cuentas por cobrar");
        return listAll().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<AccountReceivable> findById(String id) {
        logger.debug("Buscando cuenta por ID: {}", id);
        return find("id", id).stream()
                .findFirst()
                .map(mapper::toDomain);
    }

    @Override
    public Optional<AccountReceivable> findByCustomerDocument(String customerDocument) {
        logger.debug("Buscando cuenta por documento: {}", customerDocument);
        return find("customerDocument", customerDocument).stream()
                .findFirst()
                .map(mapper::toDomain);
    }

    @Override
    public List<AccountReceivable> findByStatus(AccountStatus status) {
        logger.debug("Buscando cuentas con estado: {}", status);
        return find("status", status).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<AccountReceivable> findAccountsWithDebt() {
        logger.debug("Buscando cuentas con deuda pendiente");
        return find("totalDebt > 0 ORDER BY totalDebt DESC").stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<AccountReceivable> findOverdueAccounts(int daysOverdue) {
        LocalDate cutoffDate = LocalDate.now().minusDays(daysOverdue);
        logger.debug("Buscando cuentas vencidas desde: {}", cutoffDate);

        return find("totalDebt > 0 AND lastTransactionDate <= ?1", cutoffDate).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<AccountReceivable> findByDebtRange(BigDecimal minDebt, BigDecimal maxDebt) {
        logger.debug("Buscando cuentas con deuda entre: {} - {}", minDebt, maxDebt);
        return find("totalDebt >= ?1 AND totalDebt <= ?2 ORDER BY totalDebt DESC", minDebt, maxDebt)
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<AccountReceivable> findByDateRange(LocalDate startDate, LocalDate endDate) {
        logger.debug("Buscando cuentas en rango: {} - {}", startDate, endDate);
        return find("lastTransactionDate >= ?1 AND lastTransactionDate <= ?2 ORDER BY lastTransactionDate DESC",
                startDate, endDate)
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public AccountReceivable create(AccountReceivable account) {
        logger.debug("➕ Creando nueva cuenta para: {}", account.getCustomerName());

        AccountReceivableEntity newEntity = mapper.toEntity(account);
        persist(newEntity);

        logger.info("Cuenta creada para cliente: {}", account.getCustomerName());
        return mapper.toDomain(newEntity);
    }

    @Override
    @Transactional
    public AccountReceivable update(AccountReceivable account) {
        logger.debug("🔄 Actualizando cuenta: {}", account.getId());

        AccountReceivableEntity existingEntity = find("id", account.getId()).firstResult();
        if (existingEntity == null) {
            throw new IllegalArgumentException("Account not found: " + account.getId());
        }

        mapper.updateEntity(existingEntity, account);

        // Actualizar transacciones si es necesario
        updateTransactions(account);

        logger.info("Cuenta actualizada: {}", account.getCustomerName());
        return mapper.toDomain(existingEntity);
    }

    private void updateTransactions(AccountReceivable account) {
        // Eliminar transacciones existentes y crear nuevas
        entityManager.createQuery("DELETE FROM DebtTransactionEntity t WHERE t.accountId = :accountId")
                .setParameter("accountId", account.getId())
                .executeUpdate();

        // Insertar transacciones actuales
        for (DebtTransaction transaction : account.getTransactions()) {
            DebtTransactionEntity transactionEntity = mapper.transactionToEntity(transaction, account.getId());
            entityManager.persist(transactionEntity);
        }
    }

    @Override
    @Transactional
    public void deleteById(String id) {
        logger.debug("Eliminando cuenta: {}", id);

        // Eliminar primero las transacciones
        entityManager.createQuery("DELETE FROM DebtTransactionEntity t WHERE t.accountId = :accountId")
                .setParameter("accountId", id)
                .executeUpdate();

        long deletedCount = delete("id", id);
        if (deletedCount == 0) {
            throw new IllegalArgumentException("Account not found: " + id);
        }

        logger.info("Cuenta {} eliminada", id);
    }

    @Override
    public boolean existsByCustomerDocument(String customerDocument) {
        return find("customerDocument", customerDocument).count() > 0;
    }

    @Override
    public List<DebtTransaction> findTransactionsByAccountId(String accountId) {
        logger.debug("Buscando transacciones de cuenta: {}", accountId);

        List<DebtTransactionEntity> entities = entityManager
                .createQuery("SELECT t FROM DebtTransactionEntity t WHERE t.accountId = :accountId ORDER BY t.transactionDate DESC",
                        DebtTransactionEntity.class)
                .setParameter("accountId", accountId)
                .getResultList();

        return entities.stream()
                .map(mapper::transactionToDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<DebtTransaction> findTransactionsByDateRange(LocalDate startDate, LocalDate endDate) {
        logger.debug("Buscando transacciones en rango: {} - {}", startDate, endDate);

        List<DebtTransactionEntity> entities = entityManager
                .createQuery("SELECT t FROM DebtTransactionEntity t WHERE t.transactionDate >= :start AND t.transactionDate <= :end ORDER BY t.transactionDate DESC",
                        DebtTransactionEntity.class)
                .setParameter("start", startDate)
                .setParameter("end", endDate)
                .getResultList();

        return entities.stream()
                .map(mapper::transactionToDomain)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public DebtTransaction saveTransaction(String accountId, DebtTransaction transaction) {
        logger.debug("Guardando transacción para cuenta: {}", accountId);

        DebtTransactionEntity entity = mapper.transactionToEntity(transaction, accountId);
        entityManager.persist(entity);

        return mapper.transactionToDomain(entity);
    }

    @Override
    public BigDecimal getTotalDebtAmount() {
        Object result = entityManager
                .createQuery("SELECT COALESCE(SUM(a.totalDebt), 0) FROM AccountReceivableEntity a WHERE a.totalDebt > 0")
                .getSingleResult();
        return (BigDecimal) result;
    }

    @Override
    public long countAccountsWithDebt() {
        return find("totalDebt > 0").count();
    }

    @Override
    public BigDecimal getAverageDebtAmount() {
        Object result = entityManager
                .createQuery("SELECT COALESCE(AVG(a.totalDebt), 0) FROM AccountReceivableEntity a WHERE a.totalDebt > 0")
                .getSingleResult();
        return new BigDecimal(result.toString());
    }
}
