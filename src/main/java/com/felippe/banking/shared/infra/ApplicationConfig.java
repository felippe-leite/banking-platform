package com.felippe.banking.shared.infra;

import com.felippe.banking.account.application.AccountService;
import com.felippe.banking.account.domain.AccountRepository;
import com.felippe.banking.customer.application.CustomerService;
import com.felippe.banking.customer.domain.CustomerRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfig {
    @Bean
    CustomerService customerService(CustomerRepository customers) {
        return new CustomerService(customers);
    }

    @Bean
    AccountService accountService(AccountRepository accounts, CustomerRepository customers) {
        return new AccountService(accounts, customers);
    }
}
