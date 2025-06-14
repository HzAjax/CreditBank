package ru.volodin.calculator.entity.dto.api.validation.amount;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = AmountValidator.class)
public @interface ValidAmount {
    String message() default "Loan amount exceeds allowed limit based on salary.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
