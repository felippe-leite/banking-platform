package com.felippe.banking.shared.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

/**
 * Learning tests: não testam código nosso, documentam como o Java se comporta
 * com números decimais. Ver notes/concepts/bigdecimal-dinheiro.md.
 */
class BigDecimalLearningTest {

    @Test
    void doubleCannotRepresentDecimalFractionsExactly() {
        assertThat(0.1 + 0.2).isNotEqualTo(0.3);
        assertThat(0.1 + 0.2).isEqualTo(0.30000000000000004);
    }

    @Test
    void doubleErrorsAccumulate() {
        double total = 0;
        for (int i = 0; i < 10; i++) {
            total += 0.1;
        }
        assertThat(total).isNotEqualTo(1.0);
    }

    @Test
    void bigDecimalFromDoubleInheritsTheError() {
        assertThat(new BigDecimal(0.1).toString())
                .startsWith("0.1000000000000000055511151231257827");
        assertThat(new BigDecimal("0.1").toString()).isEqualTo("0.1");
    }

    @Test
    void bigDecimalFromStringIsExact() {
        BigDecimal sum = new BigDecimal("0.1").add(new BigDecimal("0.2"));
        assertThat(sum).isEqualTo(new BigDecimal("0.3"));
    }

    @Test
    void equalsConsidersScaleButCompareToDoesNot() {
        BigDecimal twoPointZero = new BigDecimal("2.0");   // scale 1
        BigDecimal twoPointZeroZero = new BigDecimal("2.00"); // scale 2

        assertThat(twoPointZero.equals(twoPointZeroZero)).isFalse();
        assertThat(twoPointZero.compareTo(twoPointZeroZero)).isZero();
    }

    @Test
    void divisionWithInfiniteExpansionThrows() {
        assertThatThrownBy(() -> new BigDecimal("10").divide(new BigDecimal("3")))
                .isInstanceOf(ArithmeticException.class);
    }
}
