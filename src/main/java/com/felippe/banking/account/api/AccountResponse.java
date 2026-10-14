package com.felippe.banking.account.api;

import com.felippe.banking.account.domain.Account;
import com.felippe.banking.account.domain.AccountStatus;
import java.math.BigDecimal;
import java.util.UUID;

public record AccountResponse(UUID id, UUID customerId, String branch, String number,
        String digit, BigDecimal balance, AccountStatus status) {
    public static AccountResponse from(Account account) {
        return new AccountResponse(account.id().value(), account.customerId().value(),
                account.number().branch(), account.number().number(), account.number().digit(),
                account.balance().amount(), account.status());
    }
}
