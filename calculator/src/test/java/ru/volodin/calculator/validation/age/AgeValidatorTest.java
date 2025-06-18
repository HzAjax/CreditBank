package ru.volodin.calculator.validation.age;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;


class AgeValidatorTest {

    private AgeValidator validator;
    private ConstraintValidatorContext context;

    @BeforeEach
    void setUp() {
        validator = new AgeValidator();

        ReflectionTestUtils.setField(validator, "min", 20);
        ReflectionTestUtils.setField(validator, "max", 65);

        context = mock(ConstraintValidatorContext.class);

        ConstraintValidatorContext.ConstraintViolationBuilder builder = mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);
        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(builder);
        when(builder.addConstraintViolation()).thenReturn(context);
    }

    @Test
    void testValidAge_withinBounds_shouldBeValid() {
        LocalDate birthdate = LocalDate.now().minusYears(30);
        boolean result = validator.isValid(birthdate, context);
        assertThat(result).isTrue();
    }

    @Test
    void testValidAge_onMinBorder_shouldBeValid() {
        LocalDate birthdate = LocalDate.now().minusYears(20);
        boolean result = validator.isValid(birthdate, context);
        assertThat(result).isTrue();
    }

    @Test
    void testValidAge_onMaxBorder_shouldBeValid() {
        LocalDate birthdate = LocalDate.now().minusYears(65);
        boolean result = validator.isValid(birthdate, context);
        assertThat(result).isTrue();
    }

    @Test
    void testInvalidAge_tooYoung_shouldBeInvalid() {
        LocalDate birthdate = LocalDate.now().minusYears(19);
        boolean result = validator.isValid(birthdate, context);
        assertThat(result).isFalse();
        verify(context).buildConstraintViolationWithTemplate(contains("Age"));
    }

    @Test
    void testInvalidAge_tooOld_shouldBeInvalid() {
        LocalDate birthdate = LocalDate.now().minusYears(70);
        boolean result = validator.isValid(birthdate, context);
        assertThat(result).isFalse();
        verify(context).buildConstraintViolationWithTemplate(contains("Age"));
    }

    @Test
    void testNullBirthdate_shouldBeValid() {
        boolean result = validator.isValid(null, context);
        assertThat(result).isTrue();
    }
}
