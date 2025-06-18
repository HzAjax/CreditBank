package ru.volodin.calculator.service.scoring.filter.soft;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import ru.volodin.calculator.entity.dto.api.request.EmploymentDto;
import ru.volodin.calculator.entity.dto.api.request.ScoringDataDto;
import ru.volodin.calculator.entity.dto.enums.EmploymentStatus;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class WorkStatusSoftScoringFilterTest {

    private WorkStatusSoftScoringFilter filter;

    @BeforeEach
    void setUp() {
        filter = new WorkStatusSoftScoringFilter();

        ReflectionTestUtils.setField(filter, "changeRateValueSelfEmployed", new BigDecimal("2"));
        ReflectionTestUtils.setField(filter, "changeRateValueBusinessman", new BigDecimal("1"));
    }

    @Test
    void testRateDelta_selfEmployed_shouldReturn2() {
        ScoringDataDto dto = ScoringDataDto.builder()
                .employment(EmploymentDto.builder()
                        .employmentStatus(EmploymentStatus.SELF_EMPLOYED)
                        .build())
                .build();

        BigDecimal result = filter.rateDelta(dto);
        assertThat(result).isEqualByComparingTo("2");
    }

    @Test
    void testRateDelta_businessOwner_shouldReturn1() {
        ScoringDataDto dto = ScoringDataDto.builder()
                .employment(EmploymentDto.builder()
                        .employmentStatus(EmploymentStatus.BUSINESS_OWNER)
                        .build())
                .build();

        BigDecimal result = filter.rateDelta(dto);
        assertThat(result).isEqualByComparingTo("1");
    }

    @Test
    void testRateDelta_employed_shouldReturnZero() {
        ScoringDataDto dto = ScoringDataDto.builder()
                .employment(EmploymentDto.builder()
                        .employmentStatus(EmploymentStatus.EMPLOYED)
                        .build())
                .build();

        BigDecimal result = filter.rateDelta(dto);
        assertThat(result).isEqualByComparingTo("0");
    }

    @Test
    void testInsuranceDelta_shouldReturnZeroAlways() {
        ScoringDataDto dto = ScoringDataDto.builder()
                .employment(EmploymentDto.builder()
                        .employmentStatus(EmploymentStatus.BUSINESS_OWNER)
                        .build())
                .build();

        BigDecimal result = filter.insuranceDelta(dto);
        assertThat(result).isEqualByComparingTo("0");
    }
}
