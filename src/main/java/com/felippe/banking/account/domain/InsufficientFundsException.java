package com.felippe.banking.account.domain;

import com.felippe.banking.shared.domain.DomainException;

public final class InsufficientFundsException extends DomainException {

    public InsufficientFundsException() {
        super("Insufficient funds");
    }
}
