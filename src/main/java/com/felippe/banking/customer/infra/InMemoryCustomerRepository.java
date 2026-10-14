package com.felippe.banking.customer.infra;

import com.felippe.banking.customer.application.DuplicateCpfException;
import com.felippe.banking.customer.domain.Cpf;
import com.felippe.banking.customer.domain.Customer;
import com.felippe.banking.customer.domain.CustomerId;
import com.felippe.banking.customer.domain.CustomerRepository;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

/** Adaptador temporário: dados são perdidos ao encerrar o processo. */
@Repository
@Profile("in-memory")
public class InMemoryCustomerRepository implements CustomerRepository {
    private final Map<CustomerId, Customer> data = new HashMap<>();

    @Override
    public synchronized void save(Customer customer) {
        Objects.requireNonNull(customer);
        if (data.values().stream().anyMatch(existing -> existing.cpf().equals(customer.cpf())
                && !existing.id().equals(customer.id()))) {
            throw new DuplicateCpfException();
        }
        data.put(customer.id(), customer);
    }

    @Override
    public synchronized Optional<Customer> findById(CustomerId id) {
        return Optional.ofNullable(data.get(Objects.requireNonNull(id)));
    }

    @Override
    public synchronized boolean existsByCpf(Cpf cpf) {
        Objects.requireNonNull(cpf);
        return data.values().stream().anyMatch(customer -> customer.cpf().equals(cpf));
    }
}
