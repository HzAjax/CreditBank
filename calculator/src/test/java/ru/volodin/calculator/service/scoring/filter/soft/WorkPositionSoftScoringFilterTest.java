package ru.volodin.calculator.service.scoring.filter.soft;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import ru.volodin.calculator.entity.dto.api.request.EmploymentDto;
import ru.volodin.calculator.entity.dto.api.request.ScoringDataDto;
import ru.volodin.calculator.entity.dto.enums.Position;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class WorkPositionSoftScoringFilterTest {

    private WorkPositionSoftScoringFilter filter;

    @BeforeEach
    void setUp() {
        filter = new WorkPositionSoftScoringFilter();

        ReflectionTestUtils.setField(filter, "changeRateValueMiddleManager", new BigDecimal("-2"));
        ReflectionTestUtils.setField(filter, "changeRateValueTopManager", new BigDecimal("-3"));
    }

    @Test
    void testRateDelta_midManager_shouldReturnMinus2() {
        ScoringDataDto dto = ScoringDataDto.builder()
                .employment(EmploymentDto.builder()
                        .position(Position.MID_MANAGER)
                        .build())
                .build();

        BigDecimal result = filter.rateDelta(dto);
        assertThat(result).isEqualByComparingTo("-2");
    }

    @Test
    void testRateDelta_topManager_shouldReturnMinus3() {
        ScoringDataDto dto = ScoringDataDto.builder()
                .employment(EmploymentDto.builder()
                        .position(Position.TOP_MANAGER)
                        .build())
                .build();

        BigDecimal result = filter.rateDelta(dto);
        assertThat(result).isEqualByComparingTo("-3");
    }

    @Test
    void testRateDelta_worker_shouldReturnZero() {
        ScoringDataDto dto = ScoringDataDto.builder()
                .employment(EmploymentDto.builder()
                        .position(Position.MANAGER)
                        .build())
                .build();

        BigDecimal result = filter.rateDelta(dto);
        assertThat(result).isEqualByComparingTo("0");
    }

    @Test
    void testInsuranceDelta_shouldReturnZeroAlways() {
        ScoringDataDto dto = ScoringDataDto.builder()
                .employment(EmploymentDto.builder()
                        .position(Position.MID_MANAGER)
                        .build())
                .build();

        BigDecimal result = filter.insuranceDelta(dto);
        assertThat(result).isEqualByComparingTo("0");
    }
}
