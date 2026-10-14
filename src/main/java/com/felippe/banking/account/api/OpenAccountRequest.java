package com.felippe.banking.account.api;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.util.UUID;

public record OpenAccountRequest(
        @NotNull UUID customerId,
        @NotNull @Pattern(regexp = "[0-9]{4}") String branch,
        @NotNull @Pattern(regexp = "[0-9]{6}") String number,
        @NotNull @Pattern(regexp = "[0-9]") String digit) {}
