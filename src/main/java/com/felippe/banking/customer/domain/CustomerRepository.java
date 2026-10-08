package com.felippe.banking.customer.domain;

import java.util.Optional;

/**
 * Contrato de armazenamento de clientes, independente da tecnologia.
 * Todos os argumentos devem ser não nulos.
 */
public interface CustomerRepository {

    /** Salva o estado do cliente, inserindo ou atualizando pela sua identidade. */
    void save(Customer customer);

    /** Retorna vazio quando o ID não está cadastrado; nunca retorna null. */
    Optional<Customer> findById(CustomerId id);

    /** Consulta de cadastro; não substitui uma restrição de unicidade no banco. */
    boolean existsByCpf(Cpf cpf);
}
