package ru.volodin.calculator.validation.experience.total;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class ExperienceTotalValidatorTest {

    private ExperienceTotalValidator validator;
    private ConstraintValidatorContext context;

    @BeforeEach
    void setUp() {
        validator = new ExperienceTotalValidator();

        ReflectionTestUtils.setField(validator, "min", 18);

        context = mock(ConstraintValidatorContext.class);
        ConstraintValidatorContext.ConstraintViolationBuilder builder = mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);
        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(builder);
        when(builder.addConstraintViolation()).thenReturn(context);
    }

    @Test
    void testExperienceGreaterThanMin_shouldBeValid() {
        boolean result = validator.isValid(24, context);
        assertThat(result).isTrue();
    }

    @Test
    void testExperienceEqualToMin_shouldBeInvalid() {
        boolean result = validator.isValid(18, context);
        assertThat(result).isFalse();
        verify(context).buildConstraintViolationWithTemplate(contains("Total experience 18"));
    }

    @Test
    void testExperienceLessThanMin_shouldBeInvalid() {
        boolean result = validator.isValid(12, context);
        assertThat(result).isFalse();
        verify(context).buildConstraintViolationWithTemplate(contains("Total experience 12"));
    }

    @Test
    void testZeroExperience_shouldBeInvalid() {
        boolean result = validator.isValid(0, context);
        assertThat(result).isFalse();
        verify(context).buildConstraintViolationWithTemplate(contains("Total experience 0"));
    }

    @Test
    void testNegativeExperience_shouldBeInvalid() {
        boolean result = validator.isValid(-3, context);
        assertThat(result).isFalse();
        verify(context).buildConstraintViolationWithTemplate(contains("Total experience -3"));
    }

    @Test
    void testNullExperience_shouldBeValid() {
        boolean result = validator.isValid(null, context);
        assertThat(result).isTrue();
    }
}
