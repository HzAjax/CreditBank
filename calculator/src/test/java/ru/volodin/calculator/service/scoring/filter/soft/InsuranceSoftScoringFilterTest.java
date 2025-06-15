package ru.volodin.calculator.service.scoring.filter.soft;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.volodin.calculator.entity.dto.api.ScoringDataDto;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class InsuranceSoftScoringFilterTest {

    @Autowired
    private InsuranceSoftScoringFilter filter;

    private ScoringDataDto buildDto(Boolean insuranceEnabled) {
        return ScoringDataDto.builder()
                .birthdate(LocalDate.of(1990, 1, 1))
                .isInsuranceEnabled(insuranceEnabled)
                .build();
    }

    @Test
    void testRateDeltaWithInsurance() {
        ScoringDataDto dto = buildDto(true);
        assertEquals(BigDecimal.valueOf(-3), filter.rateDelta(dto));
    }

    @Test
    void testRateDeltaWithoutInsurance() {
        ScoringDataDto dto = buildDto(false);
        assertEquals(BigDecimal.ZERO, filter.rateDelta(dto));
    }

    @Test
    void testInsuranceDeltaWithInsurance() {
        ScoringDataDto dto = buildDto(true);
        assertEquals(BigDecimal.valueOf(100000), filter.insuranceDelta(dto));
    }

    @Test
    void testInsuranceDeltaWithoutInsurance() {
        ScoringDataDto dto = buildDto(false);
        assertEquals(BigDecimal.ZERO, filter.insuranceDelta(dto));
    }
}
