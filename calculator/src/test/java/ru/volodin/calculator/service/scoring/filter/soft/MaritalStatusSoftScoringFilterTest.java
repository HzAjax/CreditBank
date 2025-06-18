package ru.volodin.calculator.service.scoring.filter.soft;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import ru.volodin.calculator.entity.dto.api.request.ScoringDataDto;
import ru.volodin.calculator.entity.dto.enums.MaritalStatus;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class MaritalStatusSoftScoringFilterTest {

    private MaritalStatusSoftScoringFilter filter;

    @BeforeEach
    void setUp() {
        filter = new MaritalStatusSoftScoringFilter();

        ReflectionTestUtils.setField(filter, "changeRateValueSingleStatus", new BigDecimal("1"));
        ReflectionTestUtils.setField(filter, "changeRateValueMarriedStatus", new BigDecimal("-3"));
    }

    @Test
    void testRateDelta_single_shouldReturnIncrease() {
        ScoringDataDto dto = ScoringDataDto.builder()
                .maritalStatus(MaritalStatus.SINGLE)
                .build();

        BigDecimal result = filter.rateDelta(dto);
        assertThat(result).isEqualByComparingTo("1");
    }

    @Test
    void testRateDelta_married_shouldReturnDecrease() {
        ScoringDataDto dto = ScoringDataDto.builder()
                .maritalStatus(MaritalStatus.MARRIED)
                .build();

        BigDecimal result = filter.rateDelta(dto);
        assertThat(result).isEqualByComparingTo("-3");
    }

    @Test
    void testRateDelta_otherStatus_shouldReturnZero() {
        ScoringDataDto dto = ScoringDataDto.builder()
                .maritalStatus(MaritalStatus.DIVORCED)
                .build();

        BigDecimal result = filter.rateDelta(dto);
        assertThat(result).isEqualByComparingTo("0");
    }

    @Test
    void testInsuranceDelta_shouldAlwaysReturnZero() {
        ScoringDataDto dto = ScoringDataDto.builder()
                .maritalStatus(MaritalStatus.SINGLE)
                .build();

        BigDecimal result = filter.insuranceDelta(dto);
        assertThat(result).isEqualByComparingTo("0");
    }
}
