package com.felippe.banking.customer.application;

/** Conflito de cadastro; a mensagem não expõe o CPF do cliente. */
public final class DuplicateCpfException extends RuntimeException {

    public DuplicateCpfException() {
        super("A customer with this CPF already exists");
    }
}
