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
public class WorkStatusSoftScoringFilter implements ScoringSoftFilter {

    private final ScoringFilterProperties scoringProps;

    @Value("${scoring.filters.soft.workStatus.selfEmployed.changeRate}")
    private BigDecimal changeRateValueSelfEmployed;
    @Value("${scoring.filters.soft.workStatus.businessman.changeRate}")
    private BigDecimal changeRateValueBusinessman;

    //Рабочий статус: Самозанятый → ставка увеличивается на 2;
    //                Владелец бизнеса → ставка увеличивается на 1
    @Override
    public BigDecimal rateDelta(ScoringDataDto dto) {
        return switch (dto.getEmployment().getEmploymentStatus()) {
            case SELF_EMPLOYED -> changeRateValueSelfEmployed;
            case BUSINESS_OWNER -> changeRateValueBusinessman;
            default -> BigDecimal.ZERO;
        };
    }

    @Override
    public BigDecimal insuranceDelta(ScoringDataDto dto) {
        return BigDecimal.ZERO;
    }
}

