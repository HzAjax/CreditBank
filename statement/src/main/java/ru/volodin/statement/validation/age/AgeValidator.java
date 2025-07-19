package ru.volodin.statement.validation.age;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Slf4j
@Component
public class AgeValidator implements ConstraintValidator<ValidAge, LocalDate> {

    @Value("${prescoring.age.min}")
    private Integer min;

    @Override
    public boolean isValid(LocalDate birthdate, ConstraintValidatorContext context) {

        if (birthdate == null) {
            log.warn("Birthdate is null – skipping age validation");
            return true;
        }

        Integer age = (int) ChronoUnit.YEARS.between(birthdate, LocalDate.now());

        if (age < min) {
            context.disableDefaultConstraintViolation();

            String message = String.format("Age %d is below the minimum allowed: %d", age, min);

            context.buildConstraintViolationWithTemplate(message).addConstraintViolation();

            log.info(message);

            return false;
        }

        return true;
    }

}
