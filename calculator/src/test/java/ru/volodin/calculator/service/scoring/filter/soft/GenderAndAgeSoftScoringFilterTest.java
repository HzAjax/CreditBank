package ru.volodin.calculator.service.scoring.filter.soft;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import ru.volodin.calculator.entity.dto.api.request.ScoringDataDto;
import ru.volodin.calculator.entity.dto.enums.Gender;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class GenderAndAgeSoftScoringFilterTest {

    private GenderAndAgeSoftScoringFilter filter;

    @BeforeEach
    void setUp() {
        filter = new GenderAndAgeSoftScoringFilter();

        ReflectionTestUtils.setField(filter, "minAgeFemale", 32);
        ReflectionTestUtils.setField(filter, "maxAgeFemale", 60);
        ReflectionTestUtils.setField(filter, "minAgeMale", 30);
        ReflectionTestUtils.setField(filter, "maxAgeMale", 55);

        ReflectionTestUtils.setField(filter, "changeRateNormalGenderValue", new BigDecimal("-3"));
        ReflectionTestUtils.setField(filter, "changeRateNotBinaryValue", new BigDecimal("7"));
    }

    @Test
    void testFemaleWithinRange_shouldReturnDecreaseRate() {
        ScoringDataDto dto = ScoringDataDto.builder()
                .gender(Gender.FEMALE)
                .birthdate(LocalDate.now().minusYears(40))
                .build();

        BigDecimal delta = filter.rateDelta(dto);
        assertThat(delta).isEqualByComparingTo("-3");
    }

    @Test
    void testMaleWithinRange_shouldReturnDecreaseRate() {
        ScoringDataDto dto = ScoringDataDto.builder()
                .gender(Gender.MALE)
                .birthdate(LocalDate.now().minusYears(35))
                .build();

        BigDecimal delta = filter.rateDelta(dto);
        assertThat(delta).isEqualByComparingTo("-3");
    }

    @Test
    void testOtherGender_shouldReturnIncreaseRate() {
        ScoringDataDto dto = ScoringDataDto.builder()
                .gender(Gender.OTHER)
                .birthdate(LocalDate.now().minusYears(40))
                .build();

        BigDecimal delta = filter.rateDelta(dto);
        assertThat(delta).isEqualByComparingTo("7");
    }

    @Test
    void testOutOfRange_shouldReturnZero() {
        ScoringDataDto dto = ScoringDataDto.builder()
                .gender(Gender.MALE)
                .birthdate(LocalDate.now().minusYears(20))
                .build();

        BigDecimal delta = filter.rateDelta(dto);
        assertThat(delta).isEqualByComparingTo("0");
    }

    @Test
    void testInsuranceDelta_shouldAlwaysReturnZero() {
        ScoringDataDto dto = ScoringDataDto.builder()
                .gender(Gender.FEMALE)
                .birthdate(LocalDate.now().minusYears(40))
                .build();

        BigDecimal delta = filter.insuranceDelta(dto);
        assertThat(delta).isEqualByComparingTo("0");
    }
}