package ru.volodin.calculator.service.scoring.filter.soft;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.volodin.calculator.entity.dto.api.request.ScoringDataDto;
import ru.volodin.calculator.entity.dto.enums.Gender;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class GenderAndAgeSoftScoringFilterTest {

    @Autowired
    protected GenderAndAgeSoftScoringFilter filter;

    private ScoringDataDto createDto(Gender gender, int age) {
        return ScoringDataDto.builder()
                .gender(gender)
                .birthdate(LocalDate.now().minusYears(age))
                .build();
    }

    @Test
    void testFemaleWithinAgeRange() {
        ScoringDataDto dto = createDto(Gender.FEMALE, 40);
        assertEquals(BigDecimal.valueOf(-3), filter.rateDelta(dto));
    }

    @Test
    void testFemaleOutsideAgeRange() {
        ScoringDataDto dto = createDto(Gender.FEMALE, 25);
        assertEquals(BigDecimal.ZERO, filter.rateDelta(dto));
    }

    @Test
    void testMaleWithinAgeRange() {
        ScoringDataDto dto = createDto(Gender.MALE, 35);
        assertEquals(BigDecimal.valueOf(-3), filter.rateDelta(dto));
    }

    @Test
    void testMaleOutsideAgeRange() {
        ScoringDataDto dto = createDto(Gender.MALE, 60);
        assertEquals(BigDecimal.ZERO, filter.rateDelta(dto));
    }

    @Test
    void testOtherGender() {
        ScoringDataDto dto = createDto(Gender.OTHER, 30);
        assertEquals(BigDecimal.valueOf(7), filter.rateDelta(dto));
    }

    @Test
    void testInsuranceDeltaAlwaysZero() {
        ScoringDataDto dto = createDto(Gender.MALE, 40);
        assertEquals(BigDecimal.ZERO, filter.insuranceDelta(dto));
    }

    @Test
    void testGenderOther_shouldReturnPositiveRateDelta() {
        ScoringDataDto dto = ScoringDataDto.builder()
                .gender(Gender.OTHER)
                .birthdate(LocalDate.now().minusYears(25))
                .build();

        assertEquals(BigDecimal.valueOf(7), filter.rateDelta(dto));
    }

    @Test
    void testGenderInRangeButWrongAge_shouldReturnZero() {
        ScoringDataDto dto = ScoringDataDto.builder()
                .gender(Gender.MALE)
                .birthdate(LocalDate.now().minusYears(20))
                .build();

        assertEquals(BigDecimal.ZERO, filter.rateDelta(dto));
    }
}
