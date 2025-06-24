package org.blackequity.application.usecase;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.blackequity.application.mapper.AccountReceivableMapper;
import org.blackequity.domain.dto.AccountReceivable;
import org.blackequity.domain.dto.DebtTransaction;
import org.blackequity.domain.enums.AccountStatus;
import org.blackequity.domain.enums.PaymentMethod;
import org.blackequity.domain.repository.AccountReceivable.AccountReceivableRepository;
import org.blackequity.shared.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@ApplicationScoped
public class ManageAccountReceivableUseCase {

    private static final Logger logger = LoggerFactory.getLogger(ManageAccountReceivableUseCase.class);

    @Inject
    AccountReceivableRepository repository;

    @Inject
    AccountReceivableMapper mapper;

    @Transactional
    public AccountReceivable createAccount(CreateAccountRequest request) {
        logger.info("Creando cuenta para cliente: {}", request.getCustomerName());

        // Verificar que no exista una cuenta para este cliente
        repository.findByCustomerDocument(request.getCustomerDocument()).ifPresent(existing -> {
            throw new IllegalArgumentException("Ya existe una cuenta para el cliente: " + request.getCustomerDocument());
        });

        AccountReceivable account = AccountReceivable.createNew(
                request.getCustomerName(),
                request.getCustomerDocument(),
                request.getCustomerPhone(),
                request.getCreditLimit()
        );

        account.setNotes(request.getNotes());

        return repository.create(account);
    }

    @Transactional
    public AccountReceivable addDebt(AddDebtRequest request) {
        logger.info("Agregando deuda de ${} para cliente: {}", request.getAmount(), request.getCustomerDocument());

        AccountReceivable account = repository.findByCustomerDocument(request.getCustomerDocument())
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado: " + request.getCustomerDocument()));

        // Verificar que se pueda agregar la deuda
        if (!account.canAddDebt(request.getAmount())) {
            throw new IllegalStateException("No se puede agregar la deuda. Cuenta inactiva o excede límite de crédito");
        }

        account.addDebt(request.getAmount(), request.getDescription(), request.getReference());

        return repository.update(account);
    }

    @Transactional
    public AccountReceivable makePayment(MakePaymentRequest request) {
        logger.info("Procesando pago de ${} para cliente: {}", request.getAmount(), request.getCustomerDocument());

        AccountReceivable account = repository.findByCustomerDocument(request.getCustomerDocument())
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado: " + request.getCustomerDocument()));

        if (!account.hasDebt()) {
            throw new IllegalStateException("El cliente no tiene deudas pendientes");
        }

        PaymentMethod paymentMethod = PaymentMethod.valueOf(request.getPaymentMethod().toUpperCase());

        account.makePayment(request.getAmount(), request.getDescription(), paymentMethod);

        return repository.update(account);
    }

    public AccountReceivableDto getAccountByDocument(String customerDocument) {
        logger.debug("Consultando cuenta del cliente: {}", customerDocument);

        AccountReceivable account = repository.findByCustomerDocument(customerDocument)
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado: " + customerDocument));

        return mapper.toDto(account);
    }

//    public CustomerAccountResponse getCustomerAccountDetails(String customerDocument) {
//        logger.debug("📋 Obteniendo detalles completos de cuenta: {}", customerDocument);
//
//        AccountReceivable account = repository.findByCustomerDocument(customerDocument)
//                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado: " + customerDocument));
//
//        List<DebtTransaction> transactions = repository.findTransactionsByAccountId(account.getId());
//
//        return mapper.toCustomerResponse(account, transactions);
//    }

    public List<AccountReceivableDto> getAccountsWithDebt() {
        logger.debug("Consultando cuentas con deuda pendiente");

        List<AccountReceivable> accounts = repository.findAccountsWithDebt();
        return mapper.toDto(accounts);
    }

    public List<AccountReceivableDto> getOverdueAccounts(int daysOverdue) {
        logger.debug("Consultando cuentas vencidas con {} días", daysOverdue);

        List<AccountReceivable> accounts = repository.findOverdueAccounts(daysOverdue);
        return mapper.toDto(accounts);
    }

    public List<AccountReceivableDto> getAccountsByStatus(AccountStatus status) {
        logger.debug("Consultando cuentas con estado: {}", status);

        List<AccountReceivable> accounts = repository.findByStatus(status);
        return mapper.toDto(accounts);
    }

    @Transactional
    public AccountReceivable suspendAccount(String customerDocument, String reason) {
        logger.info("Suspendiendo cuenta del cliente: {}", customerDocument);

        AccountReceivable account = repository.findByCustomerDocument(customerDocument)
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado: " + customerDocument));

        account.suspend(reason);
        return repository.update(account);
    }

    @Transactional
    public AccountReceivable reactivateAccount(String customerDocument) {
        logger.info("Reactivando cuenta del cliente: {}", customerDocument);

        AccountReceivable account = repository.findByCustomerDocument(customerDocument)
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado: " + customerDocument));

        account.reactivate();
        return repository.update(account);
    }

    @Transactional
    public AccountReceivable closeAccount(String customerDocument, String reason) {
        logger.info("Cerrando cuenta del cliente: {}", customerDocument);

        AccountReceivable account = repository.findByCustomerDocument(customerDocument)
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado: " + customerDocument));

        if (account.hasDebt()) {
            throw new IllegalStateException("No se puede cerrar una cuenta con deuda pendiente");
        }

        account.close(reason);
        return repository.update(account);
    }

    public AccountStatsResponse getAccountStatistics() {
        logger.debug("Generando estadísticas de cuentas por cobrar");

        List<AccountReceivable> allAccounts = repository.findAllAccounts();

        AccountStatsResponse stats = new AccountStatsResponse();
        stats.setTotalAccounts(allAccounts.size());
        stats.setActiveAccounts(repository.findByStatus(AccountStatus.ACTIVE).size());
        stats.setAccountsWithDebt(repository.countAccountsWithDebt());
        stats.setSuspendedAccounts(repository.findByStatus(AccountStatus.SUSPENDED).size());
        stats.setTotalDebtAmount(repository.getTotalDebtAmount());
        stats.setAverageDebtAmount(repository.getAverageDebtAmount());

        // Calcular totales de crédito
        BigDecimal totalCreditLimit = allAccounts.stream()
                .map(AccountReceivable::getCreditLimit)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        stats.setTotalCreditLimit(totalCreditLimit);

        BigDecimal totalAvailableCredit = allAccounts.stream()
                .map(AccountReceivable::getAvailableCredit)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        stats.setTotalAvailableCredit(totalAvailableCredit);

        // Cuentas vencidas (más de 30 días)
        List<AccountReceivable> overdueAccounts = repository.findOverdueAccounts(30);
        stats.setOverdueAccounts(overdueAccounts.size());
        stats.setOverdueAmount(overdueAccounts.stream()
                .map(AccountReceivable::getTotalDebt)
                .reduce(BigDecimal.ZERO, BigDecimal::add));

        return stats;
    }

    public List<DebtTransactionDto> getTransactionHistory(String customerDocument, LocalDate startDate, LocalDate endDate) {
        logger.debug("📜 Consultando historial de transacciones: {}", customerDocument);

        AccountReceivable account = repository.findByCustomerDocument(customerDocument)
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado: " + customerDocument));

        List<DebtTransaction> transactions = repository.findTransactionsByAccountId(account.getId());

        // Filtrar por fechas si se proporcionan
        if (startDate != null && endDate != null) {
            transactions = transactions.stream()
                    .filter(t -> !t.getTransactionDate().isBefore(startDate) && !t.getTransactionDate().isAfter(endDate))
                    .collect(java.util.stream.Collectors.toList());
        }

        return mapper.transactionsToDto(transactions);
    }

    public List<AccountReceivableDto> searchAccounts(String searchTerm) {
        logger.debug("Buscando cuentas con término: {}", searchTerm);

        List<AccountReceivable> results = repository.findAllAccounts().stream()
                .filter(account ->
                        account.getCustomerName().toLowerCase().contains(searchTerm.toLowerCase()) ||
                                account.getCustomerDocument().contains(searchTerm)
                )
                .collect(java.util.stream.Collectors.toList());

        return mapper.toDto(results);
    }

    @Transactional
    public AccountReceivable updateCreditLimit(String customerDocument, BigDecimal newCreditLimit) {
        logger.info("Actualizando límite de crédito para cliente: {} a ${}", customerDocument, newCreditLimit);

        AccountReceivable account = repository.findByCustomerDocument(customerDocument)
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado: " + customerDocument));

        if (newCreditLimit.compareTo(account.getTotalDebt()) < 0) {
            throw new IllegalArgumentException("El nuevo límite no puede ser menor a la deuda actual: " + account.getTotalDebt());
        }

        account.setCreditLimit(newCreditLimit);
        return repository.update(account);
    }

    @Transactional
    public void deleteAccount(String customerDocument) {
        logger.info("Eliminando cuenta del cliente: {}", customerDocument);

        AccountReceivable account = repository.findByCustomerDocument(customerDocument)
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado: " + customerDocument));

        if (account.hasDebt()) {
            throw new IllegalStateException("No se puede eliminar una cuenta con deuda pendiente");
        }

        repository.deleteById(account.getId());
    }

    public boolean validateCustomerExists(String customerDocument) {
        return repository.existsByCustomerDocument(customerDocument);
    }

    public BigDecimal getCustomerDebtAmount(String customerDocument) {
        return repository.findByCustomerDocument(customerDocument)
                .map(AccountReceivable::getTotalDebt)
                .orElse(BigDecimal.ZERO);
    }
}