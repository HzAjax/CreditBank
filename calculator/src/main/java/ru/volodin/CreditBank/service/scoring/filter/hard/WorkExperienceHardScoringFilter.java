package ru.volodin.CreditBank.service.scoring.filter.hard;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.volodin.CreditBank.entity.dto.ScoringDataDto;
import ru.volodin.CreditBank.exeptions.ScoringException;
import ru.volodin.CreditBank.service.scoring.filter.ScoringHardFilter;

@Slf4j
@Component
public class WorkExperienceHardScoringFilter implements ScoringHardFilter {

    @Value("${scoring.filters.hard.experience.total}")
    private Integer workExperienceTotal;
    @Value("${scoring.filters.hard.experience.current}")
    private Integer workExperienceCurrent;

    //Стаж работы: Общий стаж менее 18 месяцев → отказ; Текущий стаж менее 3 месяцев → отказ
    @Override
    public boolean check(ScoringDataDto scoringDataDto) {
        boolean isTotalExperienseValid = scoringDataDto.getEmployment().getWorkExperienceTotal() >= workExperienceTotal;
        boolean isCurrentExperienceValid = scoringDataDto.getEmployment().getWorkExperienceCurrent() >= workExperienceCurrent;

        if (!isTotalExperienseValid) {
            throw new ScoringException("The total length of service is less than the required minimum of "
                    + workExperienceTotal + " months.");
        }
        if (!isCurrentExperienceValid) {
            throw new ScoringException("Current work experience is less than the required minimum "
                    + workExperienceCurrent + " months.");
        }

        return true;
    }
}
