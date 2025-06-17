package ru.volodin.calculator.validation.status;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.volodin.calculator.entity.dto.enums.EmploymentStatus;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class WorkStatusValidatorTest {

    private WorkStatusValidator validator;
    private ConstraintValidatorContext context;

    @BeforeEach
    void setUp() {
        validator = new WorkStatusValidator();

        context = mock(ConstraintValidatorContext.class);
        ConstraintValidatorContext.ConstraintViolationBuilder builder = mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);
        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(builder);
        when(builder.addConstraintViolation()).thenReturn(context);
    }

    @Test
    void testValidStatus_EMPLOYED_shouldReturnTrue() {
        boolean result = validator.isValid(EmploymentStatus.EMPLOYED, context);
        assertThat(result).isTrue();
    }

    @Test
    void testValidStatus_SELF_EMPLOYED_shouldReturnTrue() {
        boolean result = validator.isValid(EmploymentStatus.SELF_EMPLOYED, context);
        assertThat(result).isTrue();
    }

    @Test
    void testValidStatus_BUSINESS_OWNER_shouldReturnTrue() {
        boolean result = validator.isValid(EmploymentStatus.BUSINESS_OWNER, context);
        assertThat(result).isTrue();
    }

    @Test
    void testInvalidStatus_UNEMPLOYED_shouldReturnFalse() {
        boolean result = validator.isValid(EmploymentStatus.UNEMPLOYED, context);
        assertThat(result).isFalse();
        verify(context).buildConstraintViolationWithTemplate(contains("Current status UNEMPLOYED"));
    }

    @Test
    void testInvalidStatus_UNKNOWN_shouldReturnFalse() {
        boolean result = validator.isValid(EmploymentStatus.UNKNOWN, context);
        assertThat(result).isFalse();
        verify(context).buildConstraintViolationWithTemplate(contains("Current status UNKNOWN"));
    }

    @Test
    void testNullStatus_shouldReturnTrue() {
        boolean result = validator.isValid(null, context);
        assertThat(result).isTrue();
    }
}
