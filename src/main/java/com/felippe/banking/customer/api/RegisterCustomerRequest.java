package com.felippe.banking.customer.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import java.time.LocalDate;

public record RegisterCustomerRequest(
        @NotBlank String name,
        @NotBlank String cpf,
        @NotNull @PastOrPresent LocalDate birthDate) {}
