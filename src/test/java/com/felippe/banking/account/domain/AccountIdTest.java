package com.felippe.banking.account.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.felippe.banking.customer.domain.CustomerId;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class AccountIdTest {

    @Test
    void comparesUuidValuesButKeepsCustomerAndAccountIdsDistinct() {
        UUID uuid = UUID.fromString("00000000-0000-0000-0000-000000000001");
        AccountId id = new AccountId(uuid);
        assertThat(id).isEqualTo(new AccountId(uuid));
        assertThat(id.hashCode()).isEqualTo(new AccountId(uuid).hashCode());
        assertThat(id).isNotEqualTo(new CustomerId(uuid));
    }

    @Test
    void rejectsNull() {
        assertThatThrownBy(() -> new AccountId(null)).isInstanceOf(NullPointerException.class);
    }
}
