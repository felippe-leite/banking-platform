package com.felippe.banking.account.api;

import com.felippe.banking.account.application.AccountService;
import com.felippe.banking.account.application.AccountNotFoundException;
import com.felippe.banking.account.domain.AccountId;
import com.felippe.banking.account.domain.AccountNumber;
import com.felippe.banking.customer.domain.CustomerId;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/accounts")
public class AccountController {
    private final AccountService service;

    public AccountController(AccountService service) { this.service = service; }

    @PostMapping
    public ResponseEntity<AccountResponse> open(@Valid @RequestBody OpenAccountRequest request) {
        AccountResponse response = AccountResponse.from(service.open(new CustomerId(request.customerId()),
                new AccountNumber(request.branch(), request.number(), request.digit())));
        return ResponseEntity.created(URI.create("/accounts/" + response.id())).body(response);
    }

    @GetMapping("/{id}")
    public AccountResponse find(@PathVariable UUID id) {
        return AccountResponse.from(service.findById(new AccountId(id))
                .orElseThrow(AccountNotFoundException::new));
    }

    @GetMapping("/{id}/balance")
    public BalanceResponse balance(@PathVariable UUID id) {
        return new BalanceResponse(service.getBalance(new AccountId(id)).amount());
    }
}
