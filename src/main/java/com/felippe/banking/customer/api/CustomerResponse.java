package com.felippe.banking.customer.api;

import com.felippe.banking.customer.domain.Customer;
import java.time.LocalDate;
import java.util.UUID;

public record CustomerResponse(UUID id, String name, String cpf, LocalDate birthDate) {
    public static CustomerResponse from(Customer customer) {
        return new CustomerResponse(customer.id().value(), customer.name(),
                customer.cpf().value(), customer.birthDate());
    }
}
