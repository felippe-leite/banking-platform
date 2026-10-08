package com.felippe.banking.account.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.felippe.banking.customer.domain.Cpf;
import com.felippe.banking.customer.domain.Customer;
import com.felippe.banking.shared.domain.Money;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class AccountLifecycleTest {

    @Test
    void customerCanHaveIndependentAccountsAndCloseOneAfterWithdrawingItsBalance() {
        Customer customer = Customer.create("Felippe", Cpf.of("52998224725"), LocalDate.of(1995, 5, 20));
        Account first = Account.open(new AccountNumber("0001", "000001", "1"), customer.id());
        Account second = Account.open(new AccountNumber("0001", "000002", "2"), customer.id());

        assertThat(first.customerId()).isEqualTo(customer.id());
        assertThat(second.customerId()).isEqualTo(customer.id());
        assertThat(first.id()).isNotEqualTo(second.id());

        first.deposit(Money.of("100"));
        first.withdraw(Money.of("25.50"));
        assertThat(first.balance()).isEqualTo(Money.of("74.50"));
        assertThat(second.balance()).isEqualTo(Money.ZERO);

        first.block();
        first.unblock();
        first.withdraw(first.balance());
        first.close();

        assertThat(first.balance()).isEqualTo(Money.ZERO);
        assertThat(first.status()).isEqualTo(AccountStatus.CLOSED);
        assertThat(second.status()).isEqualTo(AccountStatus.ACTIVE);
        assertThat(second.balance()).isEqualTo(Money.ZERO);
    }
}
