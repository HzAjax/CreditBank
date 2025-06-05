package ru.volodin.CreditBank.service.scoring.filter.hard;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.volodin.CreditBank.entity.dto.ScoringDataDto;
import ru.volodin.CreditBank.entity.dto.enums.EmploymentStatus;
import ru.volodin.CreditBank.exeptions.ScoringException;
import ru.volodin.CreditBank.service.scoring.filter.ScoringHardFilter;

@Slf4j
@Component
public class WorkStatusHardScoringFilter implements ScoringHardFilter {

    //Рабочий статус: Безработный → отказ
    @Override
    public boolean check(ScoringDataDto scoringDataDto) {
        if (scoringDataDto.getEmployment().getEmploymentStatus() == EmploymentStatus.UNEMPLOYED) {
            throw new ScoringException("The applicant is unemployed, which does not meet the selection criteria.");
        }

        return true;
    }
}
