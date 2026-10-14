package com.felippe.banking.account.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.felippe.banking.account.domain.Account;
import com.felippe.banking.account.domain.AccountId;
import com.felippe.banking.account.domain.AccountNumber;
import com.felippe.banking.account.domain.AccountRepository;
import com.felippe.banking.account.domain.AccountStatus;
import com.felippe.banking.customer.application.CustomerNotFoundException;
import com.felippe.banking.customer.domain.Cpf;
import com.felippe.banking.customer.domain.Customer;
import com.felippe.banking.customer.domain.CustomerId;
import com.felippe.banking.customer.domain.CustomerRepository;
import com.felippe.banking.shared.domain.Money;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class AccountServiceTest {
    private static final AccountNumber NUMBER = new AccountNumber("0001", "123456", "7");
    private final Customer customer = Customer.create("Felippe", Cpf.of("52998224725"),
            LocalDate.of(1995, 5, 20));
    private final InMemoryAccounts accounts = new InMemoryAccounts();
    private final InMemoryCustomers customers = new InMemoryCustomers();
    private final AccountService service = new AccountService(accounts, customers);

    @Test
    void opensActiveAccountWithZeroBalanceForExistingCustomer() {
        customers.save(customer);
        Account account = service.open(customer.id(), NUMBER);

        assertThat(account.customerId()).isEqualTo(customer.id());
        assertThat(account.number()).isEqualTo(NUMBER);
        assertThat(account.status()).isEqualTo(AccountStatus.ACTIVE);
        assertThat(service.findById(account.id())).containsSame(account);
        assertThat(service.getBalance(account.id())).isEqualTo(Money.ZERO);
        assertThat(accounts.data).hasSize(1);
    }

    @Test
    void rejectsUnknownCustomerWithoutSavingAccount() {
        assertThatThrownBy(() -> service.open(customer.id(), NUMBER))
                .isInstanceOf(CustomerNotFoundException.class);
        assertThat(accounts.data).isEmpty();
    }

    @Test
    void rejectsDuplicateFullNumberEvenForAnotherCustomer() {
        customers.save(customer);
        Account original = service.open(customer.id(), NUMBER);
        Customer other = Customer.create("Outro cliente", Cpf.of("11144477735"),
                LocalDate.of(1990, 1, 1));
        customers.save(other);

        assertThatThrownBy(() -> service.open(other.id(), new AccountNumber("0001", "123456", "7")))
                .isInstanceOf(DuplicateAccountNumberException.class);
        assertThat(accounts.data).hasSize(1);
        assertThat(service.findById(original.id())).containsSame(original);
        assertThat(original.customerId()).isEqualTo(customer.id());
    }

    @Test
    void allowsMultipleAccountsForOneCustomerAndSameNumberInDifferentBranches() {
        customers.save(customer);
        Account first = service.open(customer.id(), NUMBER);
        Account second = service.open(customer.id(), new AccountNumber("0002", "123456", "7"));

        assertThat(first.id()).isNotEqualTo(second.id());
        assertThat(accounts.data).hasSize(2);
    }

    @Test
    void readsCurrentBalanceWithoutChangingAccountIncludingBlockedAccount() {
        customers.save(customer);
        Account account = service.open(customer.id(), NUMBER);
        account.deposit(Money.of("123.45"));
        account.block();

        assertThat(service.getBalance(account.id())).isEqualTo(Money.of("123.45"));
        assertThat(account.balance()).isEqualTo(Money.of("123.45"));
        assertThat(account.status()).isEqualTo(AccountStatus.BLOCKED);
    }

    @Test
    void distinguishesUnknownAccountFromZeroBalance() {
        AccountId unknown = AccountId.generate();
        assertThat(service.findById(unknown)).isEmpty();
        assertThatThrownBy(() -> service.getBalance(unknown))
                .isInstanceOf(AccountNotFoundException.class);
    }

    @Test
    void propagatesSaveFailure() {
        customers.save(customer);
        AccountRepository failing = new InMemoryAccounts() {
            @Override
            public void save(Account account) {
                throw new IllegalStateException("Storage unavailable");
            }
        };
        assertThatThrownBy(() -> new AccountService(failing, customers).open(customer.id(), NUMBER))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Storage unavailable");
    }

    @Test
    void rejectsMissingDependenciesAndArguments() {
        assertThatThrownBy(() -> new AccountService(null, customers)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> new AccountService(accounts, null)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> service.open(null, NUMBER)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> service.open(customer.id(), null)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> service.findById(null)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> service.getBalance(null)).isInstanceOf(NullPointerException.class);
        assertThat(accounts.data).isEmpty();
    }

    private static class InMemoryAccounts implements AccountRepository {
        private final Map<AccountId, Account> data = new HashMap<>();

        @Override
        public void save(Account account) { data.put(account.id(), account); }

        @Override
        public Optional<Account> findById(AccountId id) { return Optional.ofNullable(data.get(id)); }

        @Override
        public boolean existsByNumber(AccountNumber number) {
            return data.values().stream().anyMatch(account -> account.number().equals(number));
        }
    }

    private static class InMemoryCustomers implements CustomerRepository {
        private final Map<CustomerId, Customer> data = new HashMap<>();

        @Override
        public void save(Customer customer) { data.put(customer.id(), customer); }

        @Override
        public Optional<Customer> findById(CustomerId id) { return Optional.ofNullable(data.get(id)); }

        @Override
        public boolean existsByCpf(Cpf cpf) {
            return data.values().stream().anyMatch(customer -> customer.cpf().equals(cpf));
        }
    }
}
