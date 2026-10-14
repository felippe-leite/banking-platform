package com.felippe.banking.account.infra;

import com.felippe.banking.account.application.DuplicateAccountNumberException;
import com.felippe.banking.account.domain.Account;
import com.felippe.banking.account.domain.AccountId;
import com.felippe.banking.account.domain.AccountNumber;
import com.felippe.banking.account.domain.AccountRepository;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

/** Adaptador temporário para abertura e consultas; não oferece transações de movimentação. */
@Repository
@Profile("in-memory")
public class InMemoryAccountRepository implements AccountRepository {
    private final Map<AccountId, Account> data = new HashMap<>();

    @Override
    public synchronized void save(Account account) {
        Objects.requireNonNull(account);
        if (data.values().stream().anyMatch(existing -> existing.number().equals(account.number())
                && !existing.id().equals(account.id()))) {
            throw new DuplicateAccountNumberException();
        }
        data.put(account.id(), account);
    }

    @Override
    public synchronized Optional<Account> findById(AccountId id) {
        return Optional.ofNullable(data.get(Objects.requireNonNull(id)));
    }

    @Override
    public synchronized boolean existsByNumber(AccountNumber number) {
        Objects.requireNonNull(number);
        return data.values().stream().anyMatch(account -> account.number().equals(number));
    }
}
