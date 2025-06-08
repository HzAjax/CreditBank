package ru.volodin.CreditBank.service.scoring.filter.hard;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.volodin.CreditBank.entity.dto.ScoringDataDto;
import ru.volodin.CreditBank.exeptions.ScoringException;
import ru.volodin.CreditBank.service.scoring.filter.ScoringHardFilter;

import java.math.BigDecimal;

@Slf4j
@Component
public class AmountHardScoringFilter implements ScoringHardFilter {

    @Value("${scoring.filters.hard.countSalary}")
    private Integer countSalary;

    //Сумма займа больше, чем 24 зарплат → отказ
    @Override
    public boolean check(ScoringDataDto scoringDataDto) {
        BigDecimal maxLoanAmount = scoringDataDto.getEmployment().getSalary()
                .multiply(BigDecimal.valueOf(countSalary));

        if (scoringDataDto.getAmount().compareTo(maxLoanAmount) > 0) {
            throw new ScoringException("The requested loan amount exceeds the maximum allowable amount, which is equal to "
                    + maxLoanAmount + ", based on salary.");
        }

        return true;
    }
}
