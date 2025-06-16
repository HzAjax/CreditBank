package ru.volodin.calculator.service.scoring.filter.soft;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.volodin.calculator.configuration.ScoringFilterProperties;
import ru.volodin.calculator.entity.dto.api.request.ScoringDataDto;
import ru.volodin.calculator.service.scoring.filter.ScoringSoftFilter;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class WorkPositionSoftScoringFilter implements ScoringSoftFilter {

    private final ScoringFilterProperties scoringProps;

    @Value("${scoring.filters.soft.workPosition.middleManager.changeRate}")
    private BigDecimal changeRateValueMiddleManager;
    @Value("${scoring.filters.soft.workPosition.topManager.changeRate}")
    private BigDecimal changeRateValueTopManager;

    //Позиция на работе: Менеджер среднего звена → ставка уменьшается на 2;
    //                   Топ-менеджер → ставка уменьшается на 3
    @Override
    public BigDecimal rateDelta(ScoringDataDto dto) {
        return switch (dto.getEmployment().getPosition()) {
            case MID_MANAGER -> changeRateValueMiddleManager;
            case TOP_MANAGER -> changeRateValueTopManager;
            default -> BigDecimal.ZERO;
        };
    }

    @Override
    public BigDecimal insuranceDelta(ScoringDataDto dto) {
        return BigDecimal.ZERO;
    }
}
