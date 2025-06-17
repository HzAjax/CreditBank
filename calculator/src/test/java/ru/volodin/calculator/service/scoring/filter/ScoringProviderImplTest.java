package ru.volodin.calculator.service.scoring.filter;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.volodin.calculator.entity.dto.api.request.EmploymentDto;
import ru.volodin.calculator.entity.dto.api.request.ScoringDataDto;
import ru.volodin.calculator.entity.dto.enums.EmploymentStatus;
import ru.volodin.calculator.entity.dto.enums.Gender;
import ru.volodin.calculator.entity.dto.enums.MaritalStatus;
import ru.volodin.calculator.entity.dto.enums.Position;
import ru.volodin.calculator.entity.dto.internal.SimpleScoringInfoDto;
import ru.volodin.calculator.service.scoring.ScoringProviderImpl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ScoringProviderImplTest {

    @Autowired
    private ScoringProviderImpl scoringProvider;

    private ScoringDataDto fullValidDto() {
        EmploymentDto employment = new EmploymentDto();
        employment.setSalary(BigDecimal.valueOf(30000));
        employment.setEmploymentStatus(EmploymentStatus.SELF_EMPLOYED);
        employment.setPosition(Position.TOP_MANAGER);
        employment.setWorkExperienceTotal(20);
        employment.setWorkExperienceCurrent(5);

        ScoringDataDto dto = new ScoringDataDto();
        dto.setAmount(BigDecimal.valueOf(300000));
        dto.setTerm(12);
        dto.setBirthdate(LocalDate.of(1990, 1, 1)); // возраст 34
        dto.setGender(Gender.MALE);
        dto.setMaritalStatus(MaritalStatus.MARRIED);
        dto.setIsInsuranceEnabled(true);
        dto.setIsSalaryClient(true);
        dto.setEmployment(employment);

        return dto;
    }

    @Test
    void fullScoring_shouldCalculateAllApplicableDeltas() {
        ScoringDataDto dto = fullValidDto();

        BigDecimal[] result = scoringProvider.fullScoring(dto);

        assertThat(result[0]).isEqualByComparingTo("9");        // 21 -3 -2 -3 -3 +2 = 9
        assertThat(result[1]).isEqualByComparingTo("100000");
    }

    @Test
    void simpleScoring_shouldProduceAllCombinations() {
        List<SimpleScoringInfoDto> result = scoringProvider.simpleScoring();

        assertThat(result).hasSize(4);

        assertThat(result).anyMatch(r -> r.isInsurance() && r.isSalaryClient());
        assertThat(result).anyMatch(r -> r.isInsurance() && !r.isSalaryClient());
        assertThat(result).anyMatch(r -> !r.isInsurance() && r.isSalaryClient());
        assertThat(result).anyMatch(r -> !r.isInsurance() && !r.isSalaryClient());
    }
}
