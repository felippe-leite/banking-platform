package com.felippe.banking.customer.api;

import com.felippe.banking.customer.application.CustomerService;
import com.felippe.banking.customer.application.CustomerNotFoundException;
import com.felippe.banking.customer.domain.Cpf;
import com.felippe.banking.customer.domain.CustomerId;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/customers")
public class CustomerController {
    private final CustomerService service;

    public CustomerController(CustomerService service) { this.service = service; }

    @PostMapping
    public ResponseEntity<CustomerResponse> register(@Valid @RequestBody RegisterCustomerRequest request) {
        CustomerResponse response = CustomerResponse.from(
                service.register(request.name(), Cpf.of(request.cpf()), request.birthDate()));
        return ResponseEntity.created(URI.create("/customers/" + response.id())).body(response);
    }

    @GetMapping("/{id}")
    public CustomerResponse find(@PathVariable UUID id) {
        return CustomerResponse.from(service.findById(new CustomerId(id))
                .orElseThrow(CustomerNotFoundException::new));
    }
}
