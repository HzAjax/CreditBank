package ru.volodin.calculator.service.scoring.filter.soft;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import ru.volodin.calculator.entity.dto.api.request.ScoringDataDto;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class InsuranceSoftScoringFilterTest {

    private InsuranceSoftScoringFilter filter;

    @BeforeEach
    void setUp() {
        filter = new InsuranceSoftScoringFilter();

        ReflectionTestUtils.setField(filter, "costInsurance", new BigDecimal("100000"));
        ReflectionTestUtils.setField(filter, "changeRateValue", new BigDecimal("-3"));
    }

    @Test
    void testRateDelta_withInsurance_shouldReturnDecreaseRate() {
        ScoringDataDto dto = ScoringDataDto.builder()
                .isInsuranceEnabled(true)
                .build();

        BigDecimal result = filter.rateDelta(dto);
        assertThat(result).isEqualByComparingTo("-3");
    }

    @Test
    void testRateDelta_withoutInsurance_shouldReturnZero() {
        ScoringDataDto dto = ScoringDataDto.builder()
                .isInsuranceEnabled(false)
                .build();

        BigDecimal result = filter.rateDelta(dto);
        assertThat(result).isEqualByComparingTo("0");
    }

    @Test
    void testInsuranceDelta_withInsurance_shouldReturnCost() {
        ScoringDataDto dto = ScoringDataDto.builder()
                .isInsuranceEnabled(true)
                .build();

        BigDecimal result = filter.insuranceDelta(dto);
        assertThat(result).isEqualByComparingTo("100000");
    }

    @Test
    void testInsuranceDelta_withoutInsurance_shouldReturnZero() {
        ScoringDataDto dto = ScoringDataDto.builder()
                .isInsuranceEnabled(false)
                .build();

        BigDecimal result = filter.insuranceDelta(dto);
        assertThat(result).isEqualByComparingTo("0");
    }
}
