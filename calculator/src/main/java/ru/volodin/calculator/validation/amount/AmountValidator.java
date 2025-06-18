package ru.volodin.calculator.validation.amount;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.volodin.calculator.configuration.ScoringFilterProperties;
import ru.volodin.calculator.entity.dto.api.request.ScoringDataDto;

import java.math.BigDecimal;

@Slf4j
@Component
public class AmountValidator implements ConstraintValidator<ValidAmount, ScoringDataDto> {

    @Value("${scoring.filters.hard.countSalary}")
    private int countSalary;

    @Override
    public boolean isValid(ScoringDataDto scoringDataDto, ConstraintValidatorContext context) {

        if (scoringDataDto.getAmount() == null
                || scoringDataDto.getEmployment() == null
                || scoringDataDto.getEmployment().getSalary() == null) {

            log.warn("Amount or salary or employment is null – skipping amount validation");
            return true;
        }

        BigDecimal maxLoan = scoringDataDto.getEmployment().getSalary().multiply(BigDecimal.valueOf(countSalary));

        if (scoringDataDto.getAmount().compareTo(maxLoan) > 0) {
            context.disableDefaultConstraintViolation();

            String message = String.format("The requested loan amount exceeds the maximum allowable amount, which is equal to %s, based on salary.",
                    maxLoan);

            context.buildConstraintViolationWithTemplate(message).addPropertyNode("amount").addConstraintViolation();

            log.info(message);

            return false;
        }

        return true;
    }
}
