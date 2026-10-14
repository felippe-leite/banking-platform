package com.felippe.banking.account.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.felippe.banking.customer.application.CustomerService;
import com.felippe.banking.customer.domain.Cpf;
import java.time.LocalDate;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("in-memory")
class AccountControllerTest {
    @Autowired private MockMvc mvc;
    @Autowired private CustomerService customers;

    @Test
    void opensFindsReadsBalanceAndRejectsDuplicateAccount() throws Exception {
        UUID customerId = customers.register("Titular", Cpf.of("11144477735"),
                LocalDate.of(1990, 1, 1)).id().value();
        String request = """
                {"customerId":"%s","branch":"0001","number":"123456","digit":"7"}
                """.formatted(customerId);
        String location = mvc.perform(post("/accounts").contentType(MediaType.APPLICATION_JSON).content(request))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.customerId").value(customerId.toString()))
                .andExpect(jsonPath("$.branch").value("0001"))
                .andExpect(jsonPath("$.number").value("123456"))
                .andExpect(jsonPath("$.digit").value("7"))
                .andExpect(jsonPath("$.status").value("ACTIVE"))
                .andExpect(jsonPath("$.balance").value(0))
                .andReturn().getResponse().getHeader("Location");
        mvc.perform(get(location)).andExpect(status().isOk())
                .andExpect(jsonPath("$.customerId").value(customerId.toString()));
        mvc.perform(get(location + "/balance")).andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(0));
        mvc.perform(post("/accounts").contentType(MediaType.APPLICATION_JSON).content(request))
                .andExpect(status().isConflict()).andExpect(jsonPath("$.code").value("CONFLICT"));
    }

    @Test
    void rejectsUnknownCustomer() throws Exception {
        mvc.perform(post("/accounts").contentType(MediaType.APPLICATION_JSON).content("""
                {"customerId":"%s","branch":"0001","number":"999999","digit":"7"}
                """.formatted(UUID.randomUUID())))
                .andExpect(status().isNotFound()).andExpect(jsonPath("$.code").value("NOT_FOUND"));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "{}",
            "{\"customerId\":\"invalid\",\"branch\":\"0001\",\"number\":\"123456\",\"digit\":\"7\"}",
            "{\"customerId\":\"00000000-0000-0000-0000-000000000001\",\"branch\":\"1\",\"number\":\"123456\",\"digit\":\"7\"}",
            "{\"customerId\":\"00000000-0000-0000-0000-000000000001\",\"branch\":\"0001\",\"number\":\"123\",\"digit\":\"7\"}",
            "{\"customerId\":\"00000000-0000-0000-0000-000000000001\",\"branch\":\"0001\",\"number\":\"123456\",\"digit\":\"a\"}"
    })
    void rejectsInvalidOpeningRequest(String request) throws Exception {
        mvc.perform(post("/accounts").contentType(MediaType.APPLICATION_JSON).content(request))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.code").value("INVALID_REQUEST"));
    }

    @Test
    void returnsNotFoundForUnknownAccountAndBalance() throws Exception {
        String path = "/accounts/" + UUID.randomUUID();
        mvc.perform(get(path)).andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("NOT_FOUND"));
        mvc.perform(get(path + "/balance")).andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("NOT_FOUND"));
    }

    @Test
    void rejectsInvalidAccountId() throws Exception {
        mvc.perform(get("/accounts/invalid/balance")).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_REQUEST"));
    }
}
