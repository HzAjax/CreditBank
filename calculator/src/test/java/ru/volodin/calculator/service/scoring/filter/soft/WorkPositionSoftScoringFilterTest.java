package ru.volodin.calculator.service.scoring.filter.soft;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.volodin.calculator.entity.dto.api.EmploymentDto;
import ru.volodin.calculator.entity.dto.api.ScoringDataDto;
import ru.volodin.calculator.entity.dto.enums.Position;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class WorkPositionSoftScoringFilterTest {

    @Autowired
    private WorkPositionSoftScoringFilter filter;

    private ScoringDataDto createDto(Position position) {
        EmploymentDto employment = EmploymentDto.builder()
                .position(position)
                .build();

        return ScoringDataDto.builder()
                .employment(employment)
                .build();
    }

    @Test
    void testMiddleManagerRateDelta() {
        ScoringDataDto dto = createDto(Position.MID_MANAGER);
        assertEquals(BigDecimal.valueOf(-2), filter.rateDelta(dto));
    }

    @Test
    void testTopManagerRateDelta() {
        ScoringDataDto dto = createDto(Position.TOP_MANAGER);
        assertEquals(BigDecimal.valueOf(-3), filter.rateDelta(dto));
    }

    @Test
    void testNullPositionRateDelta() {
        ScoringDataDto dto = createDto(null);
        assertThrows(NullPointerException.class, () -> {
            filter.rateDelta(dto);
        });
    }

    @Test
    void testInsuranceDeltaAlwaysZero() {
        ScoringDataDto dto = createDto(Position.MID_MANAGER);
        assertEquals(BigDecimal.ZERO, filter.insuranceDelta(dto));
    }
}
