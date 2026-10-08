package com.felippe.banking.customer.domain;

import java.time.LocalDate;
import java.util.Objects;

/** Cliente imutável cuja identidade é definida pelo ID. */
public final class Customer {

    private final CustomerId id;
    private final String name;
    private final Cpf cpf;
    private final LocalDate birthDate;

    public Customer(CustomerId id, String name, Cpf cpf, LocalDate birthDate) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(name, "name must not be null");
        if (name.isBlank()) {
            throw new IllegalArgumentException("name must not be blank");
        }
        this.name = name.strip();
        this.cpf = Objects.requireNonNull(cpf, "cpf must not be null");
        this.birthDate = Objects.requireNonNull(birthDate, "birthDate must not be null");
        if (birthDate.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("birthDate must not be in the future");
        }
    }

    public static Customer create(String name, Cpf cpf, LocalDate birthDate) {
        return new Customer(CustomerId.generate(), name, cpf, birthDate);
    }

    public CustomerId id() {
        return id;
    }

    public String name() {
        return name;
    }

    public Cpf cpf() {
        return cpf;
    }

    public LocalDate birthDate() {
        return birthDate;
    }

    @Override
    public boolean equals(Object other) {
        return this == other || other instanceof Customer customer && id.equals(customer.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
