package ru.volodin.calculator.entity.dto.api.validation.status;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.volodin.calculator.entity.dto.enums.EmploymentStatus;

@Slf4j
@Component
@RequiredArgsConstructor
public class WorkStatusValidator implements ConstraintValidator<ValidWorkStatus, EmploymentStatus> {
    @Override
    public boolean isValid(EmploymentStatus status, ConstraintValidatorContext context) {

        if (status == null) return true;

        if (status == EmploymentStatus.UNEMPLOYED || status == EmploymentStatus.UNKNOWN) {
            context.disableDefaultConstraintViolation();

            String message = String.format("Current status %s does not match", status);

            context.buildConstraintViolationWithTemplate(message).addConstraintViolation();

            log.info(message);

            return false;
        }

        return true;
    }

}
