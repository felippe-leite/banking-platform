package com.felippe.banking.customer.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class CpfTest {

    @ParameterizedTest
    @ValueSource(strings = {"52998224725", "529.982.247-25", "11144477735", "01234567890"})
    void acceptsValidCpf(String value) {
        assertThat(Cpf.of(value).value()).matches("[0-9]{11}");
    }

    @Test
    void normalizesFormattedCpfAndPreservesLeadingZero() {
        assertThat(Cpf.of("012.345.678-90").value()).isEqualTo("01234567890");
    }

    @Test
    void sameDigitsAreEqualRegardlessOfFormatting() {
        Cpf plain = Cpf.of("52998224725");
        Cpf formatted = Cpf.of("529.982.247-25");
        assertThat(formatted).isEqualTo(plain);
        assertThat(formatted.hashCode()).isEqualTo(plain.hashCode());
    }

    @ParameterizedTest
    @ValueSource(strings = {"52998224715", "52998224724", "", "5299822472", "529982247250",
            "529x98224725", "529-982-247.25", " 52998224725", "52998224725 "})
    void rejectsInvalidDigitsOrFormat(String value) {
        assertThatThrownBy(() -> Cpf.of(value)).isInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest
    @ValueSource(strings = {"00000000000", "11111111111", "22222222222", "33333333333",
            "44444444444", "55555555555", "66666666666", "77777777777", "88888888888", "99999999999"})
    void rejectsRepeatedDigits(String value) {
        assertThatThrownBy(() -> Cpf.of(value)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void rejectsNull() {
        assertThatThrownBy(() -> Cpf.of(null)).isInstanceOf(NullPointerException.class);
    }

    @Test
    void directConstructorAlsoValidatesAndNormalizes() {
        assertThat(new Cpf("529.982.247-25").value()).isEqualTo("52998224725");
        assertThatThrownBy(() -> new Cpf("52998224724"))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
