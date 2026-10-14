package com.felippe.banking.account.application;

import com.felippe.banking.account.domain.Account;
import com.felippe.banking.account.domain.AccountId;
import com.felippe.banking.account.domain.AccountNumber;
import com.felippe.banking.account.domain.AccountRepository;
import com.felippe.banking.customer.application.CustomerNotFoundException;
import com.felippe.banking.customer.domain.CustomerId;
import com.felippe.banking.customer.domain.CustomerRepository;
import com.felippe.banking.shared.domain.Money;
import java.util.Objects;
import java.util.Optional;

/** Coordena abertura e consultas de conta usando os contratos de armazenamento. */
public final class AccountService {

    private final AccountRepository accounts;
    private final CustomerRepository customers;

    public AccountService(AccountRepository accounts, CustomerRepository customers) {
        this.accounts = Objects.requireNonNull(accounts, "accounts must not be null");
        this.customers = Objects.requireNonNull(customers, "customers must not be null");
    }

    public Account open(CustomerId customerId, AccountNumber number) {
        Objects.requireNonNull(customerId, "customerId must not be null");
        Objects.requireNonNull(number, "number must not be null");
        if (customers.findById(customerId).isEmpty()) {
            throw new CustomerNotFoundException();
        }
        if (accounts.existsByNumber(number)) {
            throw new DuplicateAccountNumberException();
        }
        Account account = Account.open(number, customerId);
        accounts.save(account);
        return account;
    }

    public Optional<Account> findById(AccountId id) {
        return accounts.findById(Objects.requireNonNull(id, "id must not be null"));
    }

    public Money getBalance(AccountId id) {
        return findById(id).orElseThrow(AccountNotFoundException::new).balance();
    }
}
