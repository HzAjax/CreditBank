package ru.volodin.calculator.entity.dto.api.validation.status;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import jakarta.validation.Validator;
import ru.volodin.calculator.entity.dto.enums.EmploymentStatus;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class WorkStatusValidatorTest {

    @Autowired
    private static Validator validator;

    @BeforeAll
    static void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    // Тестовый класс с аннотацией @ValidWorkStatus
    static class TestDto {
        @ValidWorkStatus
        EmploymentStatus status;

        public TestDto(EmploymentStatus status) {
            this.status = status;
        }
    }

    @Test
    void shouldBeValid_whenStatusIsSELF_MPLOYED() {
        TestDto dto = new TestDto(EmploymentStatus.SELF_EMPLOYED);
        Set<ConstraintViolation<TestDto>> violations = validator.validate(dto);
        assertThat(violations).isEmpty();
    }

    @Test
    void shouldBeValid_whenStatusIsNull() {
        TestDto dto = new TestDto(null);
        Set<ConstraintViolation<TestDto>> violations = validator.validate(dto);
        assertThat(violations).isEmpty();
    }

    @Test
    void shouldBeInvalid_whenStatusIsUNEMPLOYED() {
        TestDto dto = new TestDto(EmploymentStatus.UNEMPLOYED);
        Set<ConstraintViolation<TestDto>> violations = validator.validate(dto);
        assertThat(violations).isNotEmpty();
        assertThat(violations.iterator().next().getMessage())
                .contains("Current status UNEMPLOYED does not match");
    }

    @Test
    void shouldBeInvalid_whenStatusIsUNKNOWN() {
        TestDto dto = new TestDto(EmploymentStatus.UNKNOWN);
        Set<ConstraintViolation<TestDto>> violations = validator.validate(dto);
        assertThat(violations).isNotEmpty();
        assertThat(violations.iterator().next().getMessage())
                .contains("Current status UNKNOWN does not match");
    }
}
