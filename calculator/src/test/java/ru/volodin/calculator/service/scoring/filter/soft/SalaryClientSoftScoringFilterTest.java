package ru.volodin.calculator.service.scoring.filter.soft;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.volodin.calculator.entity.dto.api.ScoringDataDto;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class SalaryClientSoftScoringFilterTest {

    @Autowired
    private SalaryClientSoftScoringFilter filter;

    @Test
    void testRateDeltaSalaryClientTrue() {
        ScoringDataDto dto = ScoringDataDto.builder()
                .isSalaryClient(true)
                .build();

        assertEquals(BigDecimal.valueOf(-2), filter.rateDelta(dto));
    }

    @Test
    void testRateDeltaSalaryClientFalse() {
        ScoringDataDto dto = ScoringDataDto.builder()
                .isSalaryClient(false)
                .build();

        assertEquals(BigDecimal.ZERO, filter.rateDelta(dto));
    }

    @Test
    void testRateDeltaSalaryClientNull() {
        ScoringDataDto dto = ScoringDataDto.builder()
                .isSalaryClient(null)
                .build();

        assertEquals(BigDecimal.ZERO, filter.rateDelta(dto));
    }

    @Test
    void testInsuranceDeltaAlwaysZero() {
        ScoringDataDto dto = ScoringDataDto.builder()
                .isSalaryClient(true)
                .build();

        assertEquals(BigDecimal.ZERO, filter.insuranceDelta(dto));
    }
}