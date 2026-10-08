package com.felippe.banking.account.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.felippe.banking.customer.domain.CustomerId;
import com.felippe.banking.shared.domain.DomainException;
import com.felippe.banking.shared.domain.Money;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class AccountTest {

    private static final AccountNumber NUMBER = new AccountNumber("0001", "000123", "4");
    private static final CustomerId CUSTOMER_ID = CustomerId.generate();

    private Account openAccount() {
        return Account.open(NUMBER, CUSTOMER_ID);
    }

    @Test
    void opensActiveAccountWithZeroBalanceAndUniqueIdentity() {
        Account account = openAccount();
        assertThat(account.id().value()).isNotNull();
        assertThat(account.number()).isEqualTo(NUMBER);
        assertThat(account.customerId()).isEqualTo(CUSTOMER_ID);
        assertThat(account.balance()).isEqualTo(Money.ZERO);
        assertThat(account.status()).isEqualTo(AccountStatus.ACTIVE);
        assertThat(account).isNotEqualTo(openAccount());
    }

    @Test
    void rejectsMissingOpeningData() {
        assertThatThrownBy(() -> Account.open(null, CUSTOMER_ID)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> Account.open(NUMBER, null)).isInstanceOf(NullPointerException.class);
    }

    @Test
    void depositsAndWithdrawsExactlyWithoutChangingPreviousMoneyValue() {
        Account account = openAccount();
        account.deposit(Money.of("0.10"));
        Money previousBalance = account.balance();
        account.deposit(Money.of("0.20"));
        account.withdraw(Money.of("0.10"));
        assertThat(account.balance()).isEqualTo(Money.of("0.20"));
        assertThat(previousBalance).isEqualTo(Money.of("0.10"));
    }

    @Test
    void canWithdrawEntireBalance() {
        Account account = openAccount();
        account.deposit(Money.of("10"));
        account.withdraw(Money.of("10"));
        assertThat(account.balance()).isEqualTo(Money.ZERO);
    }

    @Test
    void insufficientFundsPreservesBalanceAndStatus() {
        Account account = openAccount();
        account.deposit(Money.of("10"));
        assertThatThrownBy(() -> account.withdraw(Money.of("10.01")))
                .isInstanceOf(InsufficientFundsException.class);
        assertThat(account.balance()).isEqualTo(Money.of("10"));
        assertThat(account.status()).isEqualTo(AccountStatus.ACTIVE);
    }

    @ParameterizedTest
    @ValueSource(strings = {"0", "-0.01", "-10"})
    void rejectsNonPositiveMovementsWithoutChangingBalance(String value) {
        Account account = openAccount();
        account.deposit(Money.of("10"));
        assertThatThrownBy(() -> account.deposit(Money.of(value))).isInstanceOf(DomainException.class);
        assertThatThrownBy(() -> account.withdraw(Money.of(value))).isInstanceOf(DomainException.class);
        assertThat(account.balance()).isEqualTo(Money.of("10"));
    }

    @Test
    void rejectsNullMovements() {
        Account account = openAccount();
        assertThatThrownBy(() -> account.deposit(null)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> account.withdraw(null)).isInstanceOf(NullPointerException.class);
        assertThat(account.balance()).isEqualTo(Money.ZERO);
    }

    @Test
    void blockedAccountCannotMoveMoneyButCanBeUnblocked() {
        Account account = openAccount();
        account.deposit(Money.of("10"));
        account.block();
        assertThat(account.status()).isEqualTo(AccountStatus.BLOCKED);
        assertThatThrownBy(() -> account.deposit(Money.of("1"))).isInstanceOf(DomainException.class);
        assertThatThrownBy(() -> account.withdraw(Money.of("1"))).isInstanceOf(DomainException.class);
        assertThat(account.balance()).isEqualTo(Money.of("10"));
        account.unblock();
        account.withdraw(Money.of("1"));
        assertThat(account.status()).isEqualTo(AccountStatus.ACTIVE);
        assertThat(account.balance()).isEqualTo(Money.of("9"));
    }

    @Test
    void rejectsInvalidBlockTransitions() {
        Account account = openAccount();
        assertThatThrownBy(account::unblock).isInstanceOf(DomainException.class);
        assertThat(account.status()).isEqualTo(AccountStatus.ACTIVE);
        account.block();
        assertThatThrownBy(account::block).isInstanceOf(DomainException.class);
        assertThat(account.status()).isEqualTo(AccountStatus.BLOCKED);
    }

    @Test
    void cannotCloseAccountWithRemainingBalanceEvenWhenBlocked() {
        Account account = openAccount();
        account.deposit(Money.of("0.01"));
        assertThatThrownBy(account::close).isInstanceOf(DomainException.class);
        assertThat(account.status()).isEqualTo(AccountStatus.ACTIVE);
        account.block();
        assertThatThrownBy(account::close).isInstanceOf(DomainException.class);
        assertThat(account.status()).isEqualTo(AccountStatus.BLOCKED);
        assertThat(account.balance()).isEqualTo(Money.of("0.01"));
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void closesZeroBalanceAccountPermanently(boolean blocked) {
        Account account = openAccount();
        if (blocked) {
            account.block();
        }
        account.close();
        assertThat(account.status()).isEqualTo(AccountStatus.CLOSED);
        assertThatThrownBy(() -> account.deposit(Money.of("1"))).isInstanceOf(DomainException.class);
        assertThatThrownBy(() -> account.withdraw(Money.of("1"))).isInstanceOf(DomainException.class);
        assertThatThrownBy(account::block).isInstanceOf(DomainException.class);
        assertThatThrownBy(account::unblock).isInstanceOf(DomainException.class);
        assertThatThrownBy(account::close).isInstanceOf(DomainException.class);
        assertThat(account.balance()).isEqualTo(Money.ZERO);
        assertThat(account.status()).isEqualTo(AccountStatus.CLOSED);
    }

    @Test
    void identityAndHashCodeRemainStableAfterStateChanges() {
        Account account = openAccount();
        AccountId id = account.id();
        int hash = account.hashCode();
        account.deposit(Money.of("1"));
        account.block();
        assertThat(account.id()).isEqualTo(id);
        assertThat(account.hashCode()).isEqualTo(hash);
    }
}
