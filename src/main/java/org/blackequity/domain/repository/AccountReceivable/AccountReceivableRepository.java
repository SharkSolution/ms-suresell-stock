package org.blackequity.domain.repository.AccountReceivable;

import org.blackequity.domain.dto.AccountReceivable;
import org.blackequity.domain.dto.DebtTransaction;
import org.blackequity.domain.enums.AccountStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AccountReceivableRepository {
    List<AccountReceivable> findAllAccounts();
    Optional<AccountReceivable> findById(String id);
    Optional<AccountReceivable> findByCustomerDocument(String customerDocument);
    List<AccountReceivable> findByStatus(AccountStatus status);
    List<AccountReceivable> findAccountsWithDebt();
    List<AccountReceivable> findOverdueAccounts(int daysOverdue);
    List<AccountReceivable> findByDebtRange(BigDecimal minDebt, BigDecimal maxDebt);
    List<AccountReceivable> findByDateRange(LocalDate startDate, LocalDate endDate);
    AccountReceivable create(AccountReceivable account);
    AccountReceivable update(AccountReceivable account);
    void deleteById(String id);
    boolean existsByCustomerDocument(String customerDocument);

    // Métodos para transacciones
    List<DebtTransaction> findTransactionsByAccountId(String accountId);
    List<DebtTransaction> findTransactionsByDateRange(LocalDate startDate, LocalDate endDate);
    DebtTransaction saveTransaction(String accountId, DebtTransaction transaction);

    // Estadísticas
    BigDecimal getTotalDebtAmount();
    long countAccountsWithDebt();
    BigDecimal getAverageDebtAmount();
}
