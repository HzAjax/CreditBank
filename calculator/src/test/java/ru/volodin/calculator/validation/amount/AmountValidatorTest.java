package ru.volodin.calculator.validation.amount;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.volodin.calculator.entity.dto.api.request.EmploymentDto;
import ru.volodin.calculator.entity.dto.api.request.ScoringDataDto;

import java.math.BigDecimal;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class AmountValidatorTest {

    @Autowired
    private Validator validator;

    private ScoringDataDto createDto(BigDecimal amount, BigDecimal salary) {
        ScoringDataDto dto = new ScoringDataDto();
        dto.setAmount(amount);

        EmploymentDto employment = new EmploymentDto();
        employment.setSalary(salary);

        dto.setEmployment(employment);
        return dto;
    }

    @Test
    void validAmount_shouldPass() {
        ScoringDataDto dto = createDto(new BigDecimal("200000"), new BigDecimal("10000"));
        Set<ConstraintViolation<ScoringDataDto>> violations = validator.validate(dto);
        assertThat(violations).isEmpty();
    }

    @Test
    void tooHighAmount_shouldFail() {
        ScoringDataDto dto = createDto(new BigDecimal("600000"), new BigDecimal("10000"));
        Set<ConstraintViolation<ScoringDataDto>> violations = validator.validate(dto);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .contains("exceeds the maximum allowable amount");
    }

    @Test
    void nullSalary_shouldPass() {
        ScoringDataDto dto = createDto(new BigDecimal("10000"), null);
        Set<ConstraintViolation<ScoringDataDto>> violations = validator.validate(dto);
        assertThat(violations).isEmpty();
    }
}
