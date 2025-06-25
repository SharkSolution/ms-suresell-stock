package org.blackequity.application.mapper;

import org.blackequity.domain.dto.AccountReceivable;
import org.blackequity.domain.dto.DebtTransaction;
import org.blackequity.shared.dto.AccountReceivableDto;
import org.blackequity.shared.dto.DebtTransactionDto;
import org.mapstruct.*;

import java.util.List;
@Mapper(
        componentModel = MappingConstants.ComponentModel.CDI,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface AccountReceivableMapper {


    @Mapping(target = "status", expression = "java(account.getStatus().name())")
    @Mapping(target = "statusDescription", expression = "java(account.getStatus().getDisplayName())")
    @Mapping(target = "lastTransactionDate", source = "lastTransactionDate", dateFormat = "yyyy-MM-dd")
    @Mapping(target = "availableCredit", expression = "java(account.getAvailableCredit())")
    @Mapping(target = "creditUtilization", expression = "java(account.getCreditUtilization())")
    @Mapping(target = "daysWithDebt", expression = "java(account.getDaysWithDebt())")
    @Mapping(target = "hasDebt", expression = "java(account.hasDebt())")
    @Mapping(target = "canAddDebt", expression = "java(account.canAddDebt(java.math.BigDecimal.valueOf(1000)))")
    @Mapping(target = "totalTransactions", expression = "java(account.getTransactions() != null ? account.getTransactions().size() : 0)")
    @Mapping(target = "recentTransactions", expression = "java(mapRecentTransactions(account.getTransactions()))")
    AccountReceivableDto toDto(AccountReceivable account);

    List<AccountReceivableDto> toDto(List<AccountReceivable> accounts);


    @Mapping(target = "type", expression = "java(transaction.getType().name())")
    @Mapping(target = "typeDescription", expression = "java(transaction.getType().getDisplayName())")
    @Mapping(target = "paymentMethod", expression = "java(transaction.getPaymentMethod() != null ? transaction.getPaymentMethod().name() : null)")
    @Mapping(target = "paymentMethodDescription", expression = "java(transaction.getPaymentMethod() != null ? transaction.getPaymentMethod().getDisplayName() : null)")
    @Mapping(target = "transactionDate", source = "transactionDate", dateFormat = "yyyy-MM-dd")
    DebtTransactionDto transactionToDto(DebtTransaction transaction);

    List<DebtTransactionDto> transactionsToDto(List<DebtTransaction> transactions);


    default List<DebtTransactionDto> mapRecentTransactions(List<DebtTransaction> transactions) {
        if (transactions == null) return null;

        return transactions.stream()
                .sorted((t1, t2) -> t2.getTransactionDate().compareTo(t1.getTransactionDate())) // Más recientes primero
                .limit(5)
                .map(this::transactionToDto)
                .collect(java.util.stream.Collectors.toList());
    }
}