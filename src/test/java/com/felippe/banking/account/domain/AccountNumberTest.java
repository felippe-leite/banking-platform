package com.felippe.banking.account.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class AccountNumberTest {

    @Test
    void preservesLeadingZerosAndComparesAllParts() {
        AccountNumber number = new AccountNumber("0001", "000123", "4");
        assertThat(number.branch()).isEqualTo("0001");
        assertThat(number.number()).isEqualTo("000123");
        assertThat(number.digit()).isEqualTo("4");
        assertThat(number).isEqualTo(new AccountNumber("0001", "000123", "4"));
        assertThat(number).isNotEqualTo(new AccountNumber("0002", "000123", "4"));
        assertThat(number).isNotEqualTo(new AccountNumber("0001", "000124", "4"));
        assertThat(number).isNotEqualTo(new AccountNumber("0001", "000123", "5"));
    }

    @ParameterizedTest
    @CsvSource({"001,000123,4", "00001,000123,4", "00a1,000123,4", "0001,00123,4",
            "0001,0000123,4", "0001,00012a,4", "0001,000123,44", "0001,000123,X",
            "'',000123,4", "0001,'',4", "0001,000123,''"})
    void rejectsInvalidFormats(String branch, String number, String digit) {
        assertThatThrownBy(() -> new AccountNumber(branch, number, digit))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void rejectsNullParts() {
        assertThatThrownBy(() -> new AccountNumber(null, "000123", "4")).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> new AccountNumber("0001", null, "4")).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> new AccountNumber("0001", "000123", null)).isInstanceOf(NullPointerException.class);
    }
}
