package com.felippe.banking.customer.domain;

import java.util.Objects;
import java.util.UUID;

/** Identificador de cliente, distinto dos identificadores de outras entidades. */
public record CustomerId(UUID value) {

    public CustomerId {
        Objects.requireNonNull(value, "value must not be null");
    }

    public static CustomerId generate() {
        return new CustomerId(UUID.randomUUID());
    }
}
