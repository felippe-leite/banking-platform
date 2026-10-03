package com.felippe.banking.shared.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class MoneyTest {

    @Test
    void alwaysHasTwoDecimalPlaces() {
        assertThat(Money.of("10").amount()).isEqualTo(new BigDecimal("10.00"));
        assertThat(Money.of("10.5").amount().scale()).isEqualTo(2);
    }

    @Test
    void sameValueWithDifferentScaleIsEqual() {
        assertThat(Money.of("2.0")).isEqualTo(Money.of("2.00"));
        assertThat(Money.of("2.0").hashCode()).isEqualTo(Money.of("2.00").hashCode());
    }

    @Test
    void addsExactly() {
        assertThat(Money.of("0.1").add(Money.of("0.2"))).isEqualTo(Money.of("0.30"));
    }

    @Test
    void subtractsExactly() {
        assertThat(Money.of("1.10").subtract(Money.of("1.00"))).isEqualTo(Money.of("0.10"));
    }

    @Test
    void zeroIsZero() {
        assertThat(Money.ZERO).isEqualTo(Money.of("0"));
    }

    @Test
    void knowsWhenItIsNegative() {
        assertThat(Money.of("-0.01").isNegative()).isTrue();
        assertThat(Money.ZERO.isNegative()).isFalse();
    }

    @Test
    void rejectsMoreThanTwoDecimalPlaces() {
        assertThatThrownBy(() -> Money.of("10.005"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void rejectsNull() {
        assertThatThrownBy(() -> Money.of((BigDecimal) null))
                .isInstanceOf(NullPointerException.class);
    }
}
