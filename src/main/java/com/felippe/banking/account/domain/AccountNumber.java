package com.felippe.banking.account.domain;

import java.util.Objects;

/** Formato didático do projeto; não valida algoritmo bancário de dígito verificador. */
public record AccountNumber(String branch, String number, String digit) {

    public AccountNumber {
        requireDigits(branch, 4, "branch");
        requireDigits(number, 6, "number");
        requireDigits(digit, 1, "digit");
    }

    private static void requireDigits(String value, int length, String field) {
        Objects.requireNonNull(value, field + " must not be null");
        if (!value.matches("[0-9]{" + length + "}")) {
            throw new IllegalArgumentException(field + " must contain " + length + " digits");
        }
    }
}
