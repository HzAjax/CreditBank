package ru.volodin.calculator.service.credit;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.volodin.calculator.entity.dto.api.response.CreditDto;
import ru.volodin.calculator.entity.dto.api.request.EmploymentDto;
import ru.volodin.calculator.entity.dto.api.request.ScoringDataDto;
import ru.volodin.calculator.entity.dto.enums.EmploymentStatus;
import ru.volodin.calculator.entity.dto.enums.Gender;
import ru.volodin.calculator.entity.dto.enums.MaritalStatus;
import ru.volodin.calculator.entity.dto.enums.Position;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class CreditCalculationImplTest {

    @Autowired
    private CreditCalculation creditCalculation;

    private ScoringDataDto buildScoringDataDto() {
        return ScoringDataDto.builder()
                .amount(BigDecimal.valueOf(300000))
                .term(12)
                .birthdate(LocalDate.of(1990, 1, 1))
                .firstName("Ivan")
                .lastName("Petrov")
                .isInsuranceEnabled(true)
                .isSalaryClient(true)
                .gender(Gender.MALE)
                .maritalStatus(MaritalStatus.MARRIED)
                .employment(
                        EmploymentDto.builder()
                                .salary(BigDecimal.valueOf(30000))
                                .employmentStatus(EmploymentStatus.SELF_EMPLOYED)
                                .position(Position.TOP_MANAGER)
                                .workExperienceTotal(20)
                                .workExperienceCurrent(5)
                                .build()
                )
                .build();
    }

    @Test
    void calculate_shouldReturnCreditWithCorrectFields() {
        ScoringDataDto dto = buildScoringDataDto();

        BigDecimal rate = BigDecimal.valueOf(16);
        BigDecimal insurance = BigDecimal.valueOf(100000);

        CreditDto credit = creditCalculation.calculate(dto, rate, insurance);

        assertThat(credit.getAmount()).isEqualByComparingTo(dto.getAmount());
        assertThat(credit.getRate()).isEqualByComparingTo(rate);
        assertThat(credit.getPsk()).isGreaterThan(dto.getAmount());
        assertThat(credit.getMonthlyPayment()).isGreaterThan(BigDecimal.ZERO);
        assertThat(credit.getPaymentSchedule()).hasSize(dto.getTerm());

        assertThat(credit.getIsInsuranceEnabled()).isTrue();
        assertThat(credit.getIsSalaryClient()).isTrue();
    }
}
