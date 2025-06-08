package ru.volodin.CreditBank.service.scoring.filter.soft;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.volodin.CreditBank.entity.dto.RateAndInsuredServiceDto;
import ru.volodin.CreditBank.entity.dto.ScoringDataDto;
import ru.volodin.CreditBank.service.scoring.filter.ScoringSoftFilter;

import java.math.BigDecimal;

@Component
public class WorkPositionSoftScoringFilter implements ScoringSoftFilter {

    @Value("${scoring.filters.soft.workPosition.middleManager.changeRate}")
    private BigDecimal changeRateValueMiddleManager;
    @Value("${scoring.filters.soft.workPosition.topManager.changeRate}")
    private BigDecimal changeRateValueTopManager;

    //Позиция на работе: Менеджер среднего звена → ставка уменьшается на 2;
    //                   Топ-менеджер → ставка уменьшается на 3
    @Override
    public RateAndInsuredServiceDto check(ScoringDataDto scoringDataDto) {
        switch (scoringDataDto.getEmployment().getPosition()) {
            case MID_MANAGER -> {
                return new RateAndInsuredServiceDto(changeRateValueMiddleManager, BigDecimal.ZERO);
            }
            case TOP_MANAGER -> {
                return new RateAndInsuredServiceDto(changeRateValueTopManager, BigDecimal.ZERO);
            }
            default -> {
                return new RateAndInsuredServiceDto(BigDecimal.ZERO, BigDecimal.ZERO);
            }
        }
    }
}
