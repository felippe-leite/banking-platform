package com.felippe.banking.customer.domain;

import java.util.Objects;

/** CPF matematicamente válido, armazenado sem pontuação. */
public record Cpf(String value) {

    public Cpf {
        Objects.requireNonNull(value, "value must not be null");
        if (!value.matches("[0-9]{11}|[0-9]{3}\\.[0-9]{3}\\.[0-9]{3}-[0-9]{2}")) {
            throw new IllegalArgumentException("CPF must contain 11 digits or use XXX.XXX.XXX-XX format");
        }
        value = value.replace(".", "").replace("-", "");

        char firstDigit = value.charAt(0);
        boolean repeated = value.chars().allMatch(digit -> digit == firstDigit);
        if (repeated || checkDigit(value, 9) != value.charAt(9) - '0'
                || checkDigit(value, 10) != value.charAt(10) - '0') {
            throw new IllegalArgumentException("CPF has invalid check digits");
        }
    }

    public static Cpf of(String value) {
        return new Cpf(value);
    }

    private static int checkDigit(String value, int length) {
        int sum = 0;
        for (int index = 0; index < length; index++) {
            sum += (value.charAt(index) - '0') * (length + 1 - index);
        }
        int remainder = sum % 11;
        return remainder < 2 ? 0 : 11 - remainder;
    }
}
