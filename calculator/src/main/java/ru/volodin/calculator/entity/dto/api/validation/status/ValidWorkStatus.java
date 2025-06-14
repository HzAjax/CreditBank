package ru.volodin.calculator.entity.dto.api.validation.status;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = WorkStatusValidator.class)
public @interface ValidWorkStatus {
    String message() default "The work status must be within the acceptable range.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
