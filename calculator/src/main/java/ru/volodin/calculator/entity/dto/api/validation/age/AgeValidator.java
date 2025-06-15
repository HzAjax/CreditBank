package ru.volodin.calculator.entity.dto.api.validation.age;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.volodin.calculator.configuration.ScoringFilterProperties;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class AgeValidator implements ConstraintValidator<ValidAge, LocalDate> {

    private final ScoringFilterProperties scoringProps;

    @Value("${scoring.filters.hard.age.min}")
    private int min;
    @Value("${scoring.filters.hard.age.max}")
    private int max;

    @Override
    public boolean isValid(LocalDate birthdate, ConstraintValidatorContext context) {

        if (birthdate == null) {
            log.warn("Birthdate is null – skipping age validation");
            return true;
        }

        int age = (int) ChronoUnit.YEARS.between(birthdate, LocalDate.now());

        if (age < min || age > max) {
            context.disableDefaultConstraintViolation();

            String message = String.format("Age %d outside the acceptable range: [%d–%d]", age, min, max);

            context.buildConstraintViolationWithTemplate(message).addConstraintViolation();

            log.info(message);

            return false;
        }

        return true;
    }

}
