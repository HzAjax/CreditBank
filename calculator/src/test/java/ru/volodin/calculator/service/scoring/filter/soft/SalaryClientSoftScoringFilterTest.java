package ru.volodin.calculator.service.scoring.filter.soft;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import ru.volodin.calculator.entity.dto.api.request.ScoringDataDto;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class SalaryClientSoftScoringFilterTest {

    private SalaryClientSoftScoringFilter filter;

    @BeforeEach
    void setUp() {
        filter = new SalaryClientSoftScoringFilter();

        ReflectionTestUtils.setField(filter, "changeRateValue", new BigDecimal("-2"));
    }

    @Test
    void testRateDelta_salaryClient_shouldReturnDecreaseRate() {
        ScoringDataDto dto = ScoringDataDto.builder()
                .isSalaryClient(true)
                .build();

        BigDecimal result = filter.rateDelta(dto);
        assertThat(result).isEqualByComparingTo("-2");
    }

    @Test
    void testRateDelta_notSalaryClient_shouldReturnZero() {
        ScoringDataDto dto = ScoringDataDto.builder()
                .isSalaryClient(false)
                .build();

        BigDecimal result = filter.rateDelta(dto);
        assertThat(result).isEqualByComparingTo("0");
    }

    @Test
    void testInsuranceDelta_shouldAlwaysReturnZero() {
        ScoringDataDto dto = ScoringDataDto.builder()
                .isSalaryClient(true)
                .build();

        BigDecimal result = filter.insuranceDelta(dto);
        assertThat(result).isEqualByComparingTo("0");
    }
}
