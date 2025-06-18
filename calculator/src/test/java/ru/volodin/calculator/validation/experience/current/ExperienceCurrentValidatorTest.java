package ru.volodin.calculator.validation.experience.current;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class ExperienceCurrentValidatorTest {

    private ExperienceCurrentValidator validator;
    private ConstraintValidatorContext context;

    @BeforeEach
    void setUp() {
        validator = new ExperienceCurrentValidator();

        ReflectionTestUtils.setField(validator, "min", 3);

        context = mock(ConstraintValidatorContext.class);

        ConstraintValidatorContext.ConstraintViolationBuilder builder = mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);
        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(builder);
        when(builder.addConstraintViolation()).thenReturn(context);
    }

    @Test
    void testExperienceGreaterThanMin_shouldBeValid() {
        boolean result = validator.isValid(4, context);
        assertThat(result).isTrue();
    }

    @Test
    void testExperienceEqualToMin_shouldBeInvalid() {
        boolean result = validator.isValid(3, context);
        assertThat(result).isFalse();
        verify(context).buildConstraintViolationWithTemplate(contains("Current experience 3"));
    }

    @Test
    void testExperienceLessThanMin_shouldBeInvalid() {
        boolean result = validator.isValid(2, context);
        assertThat(result).isFalse();
        verify(context).buildConstraintViolationWithTemplate(contains("Current experience 2"));
    }

    @Test
    void testExperienceZero_shouldBeInvalid() {
        boolean result = validator.isValid(0, context);
        assertThat(result).isFalse();
        verify(context).buildConstraintViolationWithTemplate(contains("Current experience 0"));
    }

    @Test
    void testExperienceNegative_shouldBeInvalid() {
        boolean result = validator.isValid(-1, context);
        assertThat(result).isFalse();
        verify(context).buildConstraintViolationWithTemplate(contains("Current experience -1"));
    }

    @Test
    void testExperienceNull_shouldBeValid() {
        boolean result = validator.isValid(null, context);
        assertThat(result).isTrue();
    }
}
