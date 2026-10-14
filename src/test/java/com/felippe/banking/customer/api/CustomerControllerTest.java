package com.felippe.banking.customer.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
class CustomerControllerTest {
    @Autowired private MockMvc mvc;

    @Test
    void registersFindsAndRejectsDuplicateCustomer() throws Exception {
        String request = """
                {"name":"  Felippe  ","cpf":"529.982.247-25","birthDate":"1995-05-20"}
                """;
        String location = mvc.perform(post("/customers").contentType(MediaType.APPLICATION_JSON).content(request))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.name").value("Felippe"))
                .andExpect(jsonPath("$.cpf").value("52998224725"))
                .andExpect(jsonPath("$.birthDate").value("1995-05-20"))
                .andReturn().getResponse().getHeader("Location");
        mvc.perform(get(location)).andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Felippe"));
        mvc.perform(post("/customers").contentType(MediaType.APPLICATION_JSON)
                .content(request.replace("529.982.247-25", "52998224725")))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("CONFLICT"));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "{}",
            "{\"name\":\" \",\"cpf\":\"52998224725\",\"birthDate\":\"1995-05-20\"}",
            "{\"name\":\"Felippe\",\"cpf\":\"11111111111\",\"birthDate\":\"1995-05-20\"}",
            "{\"name\":\"Felippe\",\"cpf\":\"52998224725\",\"birthDate\":\"9999-01-01\"}",
            "{\"name\":\"Felippe\",\"cpf\":\"52998224725\",\"birthDate\":\"not-a-date\"}",
            "null", "{"
    })
    void rejectsInvalidRequestWithStandardError(String request) throws Exception {
        mvc.perform(post("/customers").contentType(MediaType.APPLICATION_JSON).content(request))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_REQUEST"))
                .andExpect(jsonPath("$.message").value("Request contains invalid or missing data"))
                .andExpect(jsonPath("$.timestamp").isNotEmpty());
    }

    @Test
    void returnsNotFoundForUnknownCustomer() throws Exception {
        mvc.perform(get("/customers/" + UUID.randomUUID()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("NOT_FOUND"));
    }

    @Test
    void rejectsInvalidId() throws Exception {
        mvc.perform(get("/customers/not-a-uuid")).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_REQUEST"));
    }
}
