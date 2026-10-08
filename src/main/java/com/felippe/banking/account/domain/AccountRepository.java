package com.felippe.banking.account.domain;

import java.util.Optional;

/**
 * Contrato de armazenamento de contas, independente da tecnologia.
 * Todos os argumentos devem ser não nulos.
 */
public interface AccountRepository {

    /** Salva o estado da conta, inserindo ou atualizando pela sua identidade. */
    void save(Account account);

    /** Retorna vazio quando o ID não está cadastrado; nunca retorna null. */
    Optional<Account> findById(AccountId id);

    /** Consulta o número completo: agência, conta e dígito. */
    boolean existsByNumber(AccountNumber number);
}
