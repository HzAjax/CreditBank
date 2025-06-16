package ru.volodin.calculator.validation.amount;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.volodin.calculator.entity.dto.api.request.EmploymentDto;
import ru.volodin.calculator.entity.dto.api.request.ScoringDataDto;
import ru.volodin.calculator.entity.dto.enums.EmploymentStatus;
import ru.volodin.calculator.entity.dto.enums.Gender;
import ru.volodin.calculator.entity.dto.enums.MaritalStatus;
import ru.volodin.calculator.entity.dto.enums.Position;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class AmountValidatorTest {

    @Autowired
    private Validator validator;

    private ScoringDataDto validDto() {
        return ScoringDataDto.builder()
                .amount(new BigDecimal("100000"))
                .term(12)
                .firstName("Ivan")
                .lastName("Petrov")
                .gender(Gender.MALE)
                .birthdate(LocalDate.of(1990, 1, 1))
                .passportSeries("1234")
                .passportNumber("567890")
                .passportIssueDate(LocalDate.of(2010, 1, 1))
                .passportIssueBranch("Some branch")
                .maritalStatus(MaritalStatus.SINGLE)
                .dependentAmount(0)
                .accountNumber("40817810099910004312")
                .isInsuranceEnabled(true)
                .isSalaryClient(true)
                .employment(
                        EmploymentDto.builder()
                                .employmentStatus(EmploymentStatus.SELF_EMPLOYED)
                                .employerINN("1234567890")
                                .salary(new BigDecimal("50000"))
                                .position(Position.TOP_MANAGER)
                                .workExperienceTotal(10)
                                .workExperienceCurrent(5)
                                .build()
                )
                .build();
    }

    @Test
    void tooHighAmount_shouldFail() {
        ScoringDataDto dto = validDto();
        dto.setAmount(new BigDecimal("600000"));
        dto.getEmployment().setSalary(new BigDecimal("10000")); // 240_000 max

        Set<ConstraintViolation<ScoringDataDto>> violations = validator.validate(dto);

        boolean found = violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("amount")
                        && v.getMessage().contains("maximum allowable amount"));

        assertThat(found).isTrue();
    }

    @Test
    void nullSalary_shouldPass() {
        ScoringDataDto dto = validDto();
        dto.getEmployment().setSalary(null);

        Set<ConstraintViolation<ScoringDataDto>> violations = validator.validate(dto);

        // нет ошибки с propertyPath "amount"
        boolean hasAmountError = violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("amount"));

        assertThat(hasAmountError).isFalse();
    }

    @Test
    void validAmount_shouldPass() {
        ScoringDataDto dto = validDto();
        dto.setAmount(new BigDecimal("200000"));                   // <= 10_000 * 24 = 240_000
        dto.getEmployment().setSalary(new BigDecimal("10000"));

        Set<ConstraintViolation<ScoringDataDto>> violations = validator.validate(dto);

        // Проверим, что нет ошибок по полю "amount"
        boolean hasAmountViolation = violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("amount"));

        assertThat(hasAmountViolation).isFalse(); // return true → значит нарушений быть не должно
    }
}
