package com.felippe.banking.account.domain;

import com.felippe.banking.customer.domain.CustomerId;
import com.felippe.banking.shared.domain.DomainException;
import com.felippe.banking.shared.domain.Money;
import java.util.Objects;

/** Conta sem cheque especial; alterações passam pelas operações de domínio. */
public final class Account {

    private final AccountId id;
    private final AccountNumber number;
    private final CustomerId customerId;
    private Money balance;
    private AccountStatus status;

    private Account(AccountId id, AccountNumber number, CustomerId customerId) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.number = Objects.requireNonNull(number, "number must not be null");
        this.customerId = Objects.requireNonNull(customerId, "customerId must not be null");
        this.balance = Money.ZERO;
        this.status = AccountStatus.ACTIVE;
    }

    public static Account open(AccountNumber number, CustomerId customerId) {
        return new Account(AccountId.generate(), number, customerId);
    }

    public void deposit(Money amount) {
        requireActive();
        requirePositive(amount);
        balance = balance.add(amount);
    }

    public void withdraw(Money amount) {
        requireActive();
        requirePositive(amount);
        Money remaining = balance.subtract(amount);
        if (remaining.isNegative()) {
            throw new InsufficientFundsException();
        }
        balance = remaining;
    }

    public void block() {
        requireStatus(AccountStatus.ACTIVE);
        status = AccountStatus.BLOCKED;
    }

    public void unblock() {
        requireStatus(AccountStatus.BLOCKED);
        status = AccountStatus.ACTIVE;
    }

    public void close() {
        if (status == AccountStatus.CLOSED) {
            throw new DomainException("Account is already closed");
        }
        if (!balance.equals(Money.ZERO)) {
            throw new DomainException("Account must have zero balance to close");
        }
        status = AccountStatus.CLOSED;
    }

    private void requireActive() {
        requireStatus(AccountStatus.ACTIVE);
    }

    private void requireStatus(AccountStatus expected) {
        if (status != expected) {
            throw new DomainException("Account must be " + expected + ", but is " + status);
        }
    }

    private static void requirePositive(Money amount) {
        Objects.requireNonNull(amount, "amount must not be null");
        if (amount.amount().signum() <= 0) {
            throw new DomainException("Amount must be positive");
        }
    }

    public AccountId id() {
        return id;
    }

    public AccountNumber number() {
        return number;
    }

    public CustomerId customerId() {
        return customerId;
    }

    public Money balance() {
        return balance;
    }

    public AccountStatus status() {
        return status;
    }

    @Override
    public boolean equals(Object other) {
        return this == other || other instanceof Account account && id.equals(account.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
