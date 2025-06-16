package ru.volodin.calculator.validation.experience.total;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.volodin.calculator.validation.experience.total.ValidExperienceTotal;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ExperienceTotalValidatorTest {

    @Autowired
    private Validator validator;

    static class TestDto {
        @ValidExperienceTotal
        private Integer workExperienceTotal;

        public TestDto(Integer value) {
            this.workExperienceTotal = value;
        }

        public TestDto() {
        }
    }

    @Test
    void validExperience_shouldPass() {
        TestDto dto = new TestDto(20);
        Set<ConstraintViolation<TestDto>> violations = validator.validate(dto);
        assertThat(violations).isEmpty();
    }

    @Test
    void tooLowExperience_shouldFail() {
        TestDto dto = new TestDto(5);
        Set<ConstraintViolation<TestDto>> violations = validator.validate(dto);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .contains("Total experience");
    }

    @Test
    void nullExperience_shouldPassValidation() {
        TestDto dto = new TestDto(null);
        Set<ConstraintViolation<TestDto>> violations = validator.validate(dto);
        assertThat(violations).isEmpty();
    }
}
