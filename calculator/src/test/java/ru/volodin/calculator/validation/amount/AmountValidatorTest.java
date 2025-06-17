package ru.volodin.calculator.validation.amount;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import ru.volodin.calculator.entity.dto.api.request.EmploymentDto;
import ru.volodin.calculator.entity.dto.api.request.ScoringDataDto;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;


class AmountValidatorTest {

    private AmountValidator validator;
    private ConstraintValidatorContext context;

    @BeforeEach
    void setUp() {
        validator = new AmountValidator();

        ReflectionTestUtils.setField(validator, "countSalary", 24);

        context = mock(ConstraintValidatorContext.class);
        ConstraintValidatorContext.ConstraintViolationBuilder builder = mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);
        ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext nodeBuilder = mock(ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext.class);

        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(builder);
        when(builder.addPropertyNode("amount")).thenReturn(nodeBuilder);
        when(nodeBuilder.addConstraintViolation()).thenReturn(context);
    }

    @Test
    void testValidAmount_shouldReturnTrue() {
        var dto = ScoringDataDto.builder()
                .amount(new BigDecimal("1000000"))
                .employment(EmploymentDto.builder().salary(new BigDecimal("50000")).build())
                .build();

        boolean result = validator.isValid(dto, context);
        assertThat(result).isTrue();
    }

    @Test
    void testAmountExactlyAtMax_shouldReturnTrue() {
        var dto = ScoringDataDto.builder()
                .amount(new BigDecimal("1200000"))
                .employment(EmploymentDto.builder().salary(new BigDecimal("50000")).build())
                .build();

        boolean result = validator.isValid(dto, context);
        assertThat(result).isTrue();
    }

    @Test
    void testAmountAboveMax_shouldReturnFalse() {
        var dto = ScoringDataDto.builder()
                .amount(new BigDecimal("1250000"))
                .employment(EmploymentDto.builder().salary(new BigDecimal("50000")).build())
                .build();

        boolean result = validator.isValid(dto, context);
        assertThat(result).isFalse();
        verify(context).buildConstraintViolationWithTemplate(contains("The requested loan amount exceeds"));
    }

    @Test
    void testNullAmount_shouldReturnTrue() {
        var dto = ScoringDataDto.builder()
                .amount(null)
                .employment(EmploymentDto.builder().salary(new BigDecimal("50000")).build())
                .build();

        boolean result = validator.isValid(dto, context);
        assertThat(result).isTrue();
    }

    @Test
    void testNullSalary_shouldReturnTrue() {
        var dto = ScoringDataDto.builder()
                .amount(new BigDecimal("100000"))
                .employment(EmploymentDto.builder().salary(null).build())
                .build();

        boolean result = validator.isValid(dto, context);
        assertThat(result).isTrue();
    }

    @Test
    void testNullEmployment_shouldReturnTrue() {
        var dto = ScoringDataDto.builder()
                .amount(new BigDecimal("100000"))
                .employment(null)
                .build();

        boolean result = validator.isValid(dto, context);
        assertThat(result).isTrue();
    }
}
