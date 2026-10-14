package com.felippe.banking.customer.application;

import com.felippe.banking.customer.domain.Cpf;
import com.felippe.banking.customer.domain.Customer;
import com.felippe.banking.customer.domain.CustomerId;
import com.felippe.banking.customer.domain.CustomerRepository;
import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;

/** Coordena os casos de uso de cliente usando o contrato de armazenamento. */
public final class CustomerService {

    private final CustomerRepository repository;

    public CustomerService(CustomerRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository must not be null");
    }

    public Customer register(String name, Cpf cpf, LocalDate birthDate) {
        Customer customer = Customer.create(name, cpf, birthDate);
        if (repository.existsByCpf(customer.cpf())) {
            throw new DuplicateCpfException();
        }
        repository.save(customer);
        return customer;
    }

    public Optional<Customer> findById(CustomerId id) {
        return repository.findById(Objects.requireNonNull(id, "id must not be null"));
    }
}
