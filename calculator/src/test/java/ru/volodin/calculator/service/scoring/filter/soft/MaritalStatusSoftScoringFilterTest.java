package ru.volodin.calculator.service.scoring.filter.soft;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.volodin.calculator.entity.dto.api.ScoringDataDto;
import ru.volodin.calculator.entity.dto.enums.MaritalStatus;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class MaritalStatusSoftScoringFilterTest {

    @Autowired
    private MaritalStatusSoftScoringFilter filter;

    @Test
    void testRateDeltaSingle() {
        ScoringDataDto dto = ScoringDataDto.builder()
                .maritalStatus(MaritalStatus.SINGLE)
                .build();

        assertEquals(BigDecimal.valueOf(1), filter.rateDelta(dto));
    }

    @Test
    void testRateDeltaMarried() {
        ScoringDataDto dto = ScoringDataDto.builder()
                .maritalStatus(MaritalStatus.MARRIED)
                .build();

        assertEquals(BigDecimal.valueOf(-3), filter.rateDelta(dto));
    }

    @Test
    void testRateDeltaDivorced() {
        ScoringDataDto dto = ScoringDataDto.builder()
                .maritalStatus(MaritalStatus.DIVORCED)
                .build();

        assertEquals(BigDecimal.ZERO, filter.rateDelta(dto));
    }

    @Test
    void testInsuranceDeltaAlwaysZero() {
        ScoringDataDto dto = ScoringDataDto.builder()
                .maritalStatus(MaritalStatus.SINGLE)
                .build();

        assertEquals(BigDecimal.ZERO, filter.insuranceDelta(dto));
    }
}