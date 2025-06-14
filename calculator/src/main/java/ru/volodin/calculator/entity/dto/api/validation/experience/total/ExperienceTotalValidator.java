package ru.volodin.calculator.entity.dto.api.validation.experience.total;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.volodin.calculator.configuration.ScoringFilterProperties;

@Slf4j
@Component
@RequiredArgsConstructor
public class ExperienceTotalValidator implements ConstraintValidator<ValidExperienceTotal, Integer> {

    private final ScoringFilterProperties scoringProps;

    @Value("${scoring.filters.hard.experience.total}")
    private int min;

    @Override
    public boolean isValid(Integer workExperienceTotal, ConstraintValidatorContext context) {

        if (workExperienceTotal <= min) {
            context.disableDefaultConstraintViolation();

            String message = String.format("Total experience %d ess than acceptable %d", workExperienceTotal, min);

            context.buildConstraintViolationWithTemplate(message).addConstraintViolation();

            log.info(message);

            return false;
        }

        return true;
    }

}
