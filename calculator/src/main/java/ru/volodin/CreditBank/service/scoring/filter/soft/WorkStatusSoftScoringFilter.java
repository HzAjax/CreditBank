package ru.volodin.CreditBank.service.scoring.filter.soft;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.volodin.CreditBank.entity.dto.RateAndInsuredServiceDto;
import ru.volodin.CreditBank.entity.dto.ScoringDataDto;
import ru.volodin.CreditBank.service.scoring.filter.ScoringSoftFilter;

import java.math.BigDecimal;

@Component
public class WorkStatusSoftScoringFilter implements ScoringSoftFilter {

    @Value("${scoring.filters.soft.workStatus.selfEmployed.changeRate}")
    private BigDecimal changeRateValueSelfEmployed;
    @Value("${scoring.filters.soft.workStatus.businessman.changeRate}")
    private BigDecimal changeRateValueBusinessman;

    //Рабочий статус: Самозанятый → ставка увеличивается на 2;
    //                Владелец бизнеса → ставка увеличивается на 1
    @Override
    public RateAndInsuredServiceDto check(ScoringDataDto scoringDataDto) {
        switch (scoringDataDto.getEmployment().getEmploymentStatus()) {
            case SELF_EMPLOYED -> {
                return new RateAndInsuredServiceDto(changeRateValueSelfEmployed, BigDecimal.ZERO);
            }
            case BUSINESS_OWNER -> {
                return new RateAndInsuredServiceDto(changeRateValueBusinessman, BigDecimal.ZERO);
            }
            default -> {
                return new RateAndInsuredServiceDto(BigDecimal.ZERO, BigDecimal.ZERO);
            }
        }
    }
}

