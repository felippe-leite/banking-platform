package com.felippe.banking.account.application;

public final class DuplicateAccountNumberException extends RuntimeException {

    public DuplicateAccountNumberException() {
        super("An account with this number already exists");
    }
}
