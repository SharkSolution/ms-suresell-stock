package org.blackequity.domain.enums;

public enum PaymentMethod {
    CASH("Efectivo"),
    CARD("Tarjeta"),
    TRANSFER("Transferencia"),
    CHECK("Cheque"),
    OTHER("Otro");

    private final String displayName;

    PaymentMethod(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
