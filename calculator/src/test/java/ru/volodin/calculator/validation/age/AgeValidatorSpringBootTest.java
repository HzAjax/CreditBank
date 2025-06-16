package ru.volodin.calculator.validation.age;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.volodin.calculator.validation.age.ValidAge;

import java.time.LocalDate;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class AgeValidatorSpringBootTest {

    @Autowired
    private Validator validator;

    static class TestDto {
        @ValidAge
        private LocalDate birthdate;

        public TestDto(LocalDate birthdate) {
            this.birthdate = birthdate;
        }

        public TestDto() {
        }
    }

    @Test
    void validAge_shouldPassValidation() {
        TestDto dto = new TestDto(LocalDate.now().minusYears(30));
        Set<ConstraintViolation<TestDto>> violations = validator.validate(dto);
        assertThat(violations).isEmpty();
    }

    @Test
    void tooYoungAge_shouldFailValidation() {
        TestDto dto = new TestDto(LocalDate.now().minusYears(10));
        Set<ConstraintViolation<TestDto>> violations = validator.validate(dto);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .contains("outside the acceptable range");
    }

    @Test
    void tooOldAge_shouldFailValidation() {
        TestDto dto = new TestDto(LocalDate.now().minusYears(100));
        Set<ConstraintViolation<TestDto>> violations = validator.validate(dto);
        assertThat(violations).hasSize(1);
    }

    @Test
    void nullBirthdate_shouldPassValidation() {
        TestDto dto = new TestDto(null);
        Set<ConstraintViolation<TestDto>> violations = validator.validate(dto);
        assertThat(violations).isEmpty();
    }
}
