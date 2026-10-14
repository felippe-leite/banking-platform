package com.felippe.banking.customer.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.felippe.banking.customer.domain.Cpf;
import com.felippe.banking.customer.domain.Customer;
import com.felippe.banking.customer.domain.CustomerId;
import com.felippe.banking.customer.domain.CustomerRepository;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class CustomerServiceTest {

    private static final Cpf CPF = Cpf.of("52998224725");
    private static final LocalDate BIRTH_DATE = LocalDate.of(1995, 5, 20);
    private final InMemoryCustomerRepository repository = new InMemoryCustomerRepository();
    private final CustomerService service = new CustomerService(repository);

    @Test
    void registersAndFindsCustomerWithValidatedData() {
        Customer customer = service.register("  Felippe Silva  ", CPF, BIRTH_DATE);

        assertThat(customer.name()).isEqualTo("Felippe Silva");
        assertThat(customer.cpf()).isEqualTo(CPF);
        assertThat(customer.birthDate()).isEqualTo(BIRTH_DATE);
        assertThat(service.findById(customer.id())).containsSame(customer);
        assertThat(repository.customers).hasSize(1);
    }

    @Test
    void rejectsDuplicateCpfRegardlessOfFormattingAndPreservesOriginalCustomer() {
        Customer original = service.register("Felippe", CPF, BIRTH_DATE);

        assertThatThrownBy(() -> service.register("Outro nome", Cpf.of("529.982.247-25"), BIRTH_DATE))
                .isInstanceOf(DuplicateCpfException.class)
                .hasMessage("A customer with this CPF already exists");

        assertThat(repository.customers).hasSize(1);
        assertThat(service.findById(original.id())).containsSame(original);
        assertThat(original.name()).isEqualTo("Felippe");
    }

    @Test
    void returnsEmptyForUnknownCustomer() {
        assertThat(service.findById(CustomerId.generate())).isEmpty();
    }

    @Test
    void rejectsInvalidRegistrationBeforeAccessingRepository() {
        assertThatThrownBy(() -> service.register(" ", CPF, BIRTH_DATE))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> service.register("Felippe", CPF, LocalDate.of(9999, 1, 1)))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> service.register("Felippe", null, BIRTH_DATE))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> service.register(null, CPF, BIRTH_DATE))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> service.register("Felippe", CPF, null))
                .isInstanceOf(NullPointerException.class);

        assertThat(repository.cpfQueries).isZero();
        assertThat(repository.customers).isEmpty();
    }

    @Test
    void propagatesStorageFailureWithoutReportingSuccess() {
        CustomerRepository failingRepository = new InMemoryCustomerRepository() {
            @Override
            public void save(Customer customer) {
                throw new IllegalStateException("Storage unavailable");
            }
        };
        CustomerService failingService = new CustomerService(failingRepository);

        assertThatThrownBy(() -> failingService.register("Felippe", CPF, BIRTH_DATE))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Storage unavailable");
    }

    @Test
    void rejectsMissingRepositoryAndLookupId() {
        assertThatThrownBy(() -> new CustomerService(null)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> service.findById(null)).isInstanceOf(NullPointerException.class);
    }

    /** Implementação apenas para testes; não representa concorrência ou persistência durável. */
    private static class InMemoryCustomerRepository implements CustomerRepository {
        private final Map<CustomerId, Customer> customers = new HashMap<>();
        private int cpfQueries;

        @Override
        public void save(Customer customer) {
            customers.put(customer.id(), customer);
        }

        @Override
        public Optional<Customer> findById(CustomerId id) {
            return Optional.ofNullable(customers.get(id));
        }

        @Override
        public boolean existsByCpf(Cpf cpf) {
            cpfQueries++;
            return customers.values().stream().anyMatch(customer -> customer.cpf().equals(cpf));
        }
    }
}
