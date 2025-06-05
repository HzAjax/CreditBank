package ru.volodin.CreditBank.service.scoring.filter.hard;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.volodin.CreditBank.entity.dto.LoanStatementRequestDto;
import ru.volodin.CreditBank.entity.dto.ScoringDataDto;
import ru.volodin.CreditBank.exeptions.ScoringException;
import ru.volodin.CreditBank.service.scoring.filter.ScoringHardFilter;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Slf4j
@Component
public class AgeHardScoringFilter implements ScoringHardFilter {

    @Value("${scoring.filters.hard.age.max}")
    private Integer maxAge;
    @Value("${scoring.filters.hard.age.min}")
    private Integer minAge;

    //Возраст менее 20 или более 65 лет → отказ
    @Override
    public boolean check(ScoringDataDto scoringDataDto) {
        LocalDate now = LocalDate.now();
        long age = ChronoUnit.YEARS.between(scoringDataDto.getBirthdate(), now);

        if (age < minAge) {
            throw new ScoringException("Age below minimum - " + minAge);
        }
        if (age > maxAge) {
            throw new ScoringException("Age above the maximum - "+ maxAge);
        }

        return true;
    }

    public boolean check(LoanStatementRequestDto loanStatementRequestDto) {
        LocalDate now = LocalDate.now();
        long age = ChronoUnit.YEARS.between(loanStatementRequestDto.getBirthdate(), now);

        if (age < minAge) {
            throw new ScoringException("Age below minimum - " + minAge);
        }
        if (age > maxAge) {
            throw new ScoringException("Age above the maximum - "+ maxAge);
        }

        return true;
    }
}
