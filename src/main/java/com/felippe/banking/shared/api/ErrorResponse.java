package com.felippe.banking.shared.api;

import java.time.Instant;

public record ErrorResponse(String code, String message, Instant timestamp) {}
