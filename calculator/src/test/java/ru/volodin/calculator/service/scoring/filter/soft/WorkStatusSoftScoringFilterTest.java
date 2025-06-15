package ru.volodin.calculator.service.scoring.filter.soft;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.volodin.calculator.entity.dto.api.EmploymentDto;
import ru.volodin.calculator.entity.dto.api.ScoringDataDto;
import ru.volodin.calculator.entity.dto.enums.EmploymentStatus;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class WorkStatusSoftScoringFilterTest {


    @Autowired
    private WorkStatusSoftScoringFilter filter;

    private ScoringDataDto createDto(EmploymentStatus status) {
        EmploymentDto employment = EmploymentDto.builder()
                .employmentStatus(status)
                .build();

        return ScoringDataDto.builder()
                .employment(employment)
                .build();
    }

    @Test
    void testBusinessOwnerRateDelta() {
        ScoringDataDto dto = createDto(EmploymentStatus.BUSINESS_OWNER);
        assertEquals(BigDecimal.valueOf(1), filter.rateDelta(dto));
    }

    @Test
    void testSelfEmployedDelta() {
        ScoringDataDto dto = createDto(EmploymentStatus.SELF_EMPLOYED);
        assertEquals(BigDecimal.valueOf(2), filter.rateDelta(dto));
    }

    @Test
    void testNullStatusRateDelta() {
        ScoringDataDto dto = createDto(null);
        assertThrows(NullPointerException.class, () -> {
            filter.rateDelta(dto);
        });
    }

    @Test
    void testInsuranceDeltaAlwaysZero() {
        ScoringDataDto dto = createDto(EmploymentStatus.SELF_EMPLOYED);
        assertEquals(BigDecimal.ZERO, filter.insuranceDelta(dto));
    }
}

