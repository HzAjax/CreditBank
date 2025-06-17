package ru.volodin.calculator.validation.experience.current;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.volodin.calculator.configuration.ScoringFilterProperties;

@Slf4j
@Component
public class ExperienceCurrentValidator implements ConstraintValidator<ValidExperienceCurrent, Integer> {

    @Value("${scoring.filters.hard.experience.current}")
    private int min;

    @Override
    public boolean isValid(Integer workExperienceCurrent, ConstraintValidatorContext context) {

        if (workExperienceCurrent == null) {
            log.warn("ExperienceCurrent is null – skipping ExperienceCurrent validation");
            return true;
        }

        if (workExperienceCurrent <= min) {
            context.disableDefaultConstraintViolation();

            String message = String.format("Current experience %d ess than acceptable %d", workExperienceCurrent, min);

            context.buildConstraintViolationWithTemplate(message).addConstraintViolation();

            log.info(message);

            return false;
        }

        return true;
    }

}
