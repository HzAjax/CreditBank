package ru.volodin.calculator.validation.experience.current;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.volodin.calculator.validation.experience.current.ValidExperienceCurrent;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ExperienceCurrentValidatorTest {

    @Autowired
    private Validator validator;

    static class TestDto {
        @ValidExperienceCurrent
        private Integer workExperienceCurrent;

        public TestDto(Integer value) {
            this.workExperienceCurrent = value;
        }

        public TestDto() {
        }
    }

    @Test
    void validExperience_shouldPass() {
        TestDto dto = new TestDto(10);
        Set<ConstraintViolation<TestDto>> violations = validator.validate(dto);
        assertThat(violations).isEmpty();
    }

    @Test
    void tooLowExperience_shouldFail() {
        TestDto dto = new TestDto(1);
        Set<ConstraintViolation<TestDto>> violations = validator.validate(dto);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .contains("Current experience");
    }

    @Test
    void nullExperience_shouldPassValidation() {
        TestDto dto = new TestDto(null);
        Set<ConstraintViolation<TestDto>> violations = validator.validate(dto);
        assertThat(violations).isEmpty();
    }
}
