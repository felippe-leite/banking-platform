package com.felippe.banking.customer.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class CustomerTest {

    private static final CustomerId ID = new CustomerId(UUID.fromString("00000000-0000-0000-0000-000000000001"));
    private static final Cpf CPF = Cpf.of("52998224725");
    private static final LocalDate BIRTH_DATE = LocalDate.of(1995, 5, 20);

    @Test
    void preservesCustomerDataAndTrimsNameEdges() {
        Customer customer = new Customer(ID, "  Felippe Silva  ", CPF, BIRTH_DATE);
        assertThat(customer.id()).isEqualTo(ID);
        assertThat(customer.name()).isEqualTo("Felippe Silva");
        assertThat(customer.cpf()).isEqualTo(CPF);
        assertThat(customer.birthDate()).isEqualTo(BIRTH_DATE);
    }

    @Test
    void createsNewCustomersWithDistinctIds() {
        Customer first = Customer.create("Felippe", CPF, BIRTH_DATE);
        Customer second = Customer.create("Felippe", CPF, BIRTH_DATE);
        assertThat(first.id().value()).isNotNull();
        assertThat(first.id()).isNotEqualTo(second.id());
        assertThat(first).isNotEqualTo(second);
    }

    @Test
    void identityDependsOnIdRatherThanRegistrationData() {
        Customer original = new Customer(ID, "Felippe", CPF, BIRTH_DATE);
        Customer updated = new Customer(ID, "Felippe Silva", CPF, BIRTH_DATE);
        assertThat(original).isEqualTo(updated);
        assertThat(original.hashCode()).isEqualTo(updated.hashCode());
        assertThat(original).isNotEqualTo(null);
        assertThat(original).isNotEqualTo(ID);
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "\t\n", "\u2003"})
    void rejectsBlankName(String name) {
        assertThatThrownBy(() -> new Customer(ID, name, CPF, BIRTH_DATE))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void rejectsMissingRequiredData() {
        assertThatThrownBy(() -> new Customer(null, "Felippe", CPF, BIRTH_DATE))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> new Customer(ID, null, CPF, BIRTH_DATE))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> new Customer(ID, "Felippe", null, BIRTH_DATE))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> new Customer(ID, "Felippe", CPF, null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void rejectsFutureBirthDate() {
        assertThatThrownBy(() -> new Customer(ID, "Felippe", CPF, LocalDate.of(9999, 12, 31)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void acceptsBirthDateTodayWithoutMinimumAgeRule() {
        LocalDate today = LocalDate.now();
        assertThat(new Customer(ID, "Felippe", CPF, today).birthDate()).isEqualTo(today);
    }

    @Test
    void customerIdComparesUuidValuesAndRejectsNull() {
        assertThat(new CustomerId(ID.value())).isEqualTo(ID);
        assertThat(new CustomerId(ID.value()).hashCode()).isEqualTo(ID.hashCode());
        assertThatThrownBy(() -> new CustomerId(null)).isInstanceOf(NullPointerException.class);
    }
}
