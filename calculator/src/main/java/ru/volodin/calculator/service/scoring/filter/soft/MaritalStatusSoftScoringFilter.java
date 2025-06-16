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
public class MaritalStatusSoftScoringFilter implements ScoringSoftFilter {

    private final ScoringFilterProperties scoringProps;

    @Value("${scoring.filters.soft.maritalStatus.single.changeRate}")
    private BigDecimal changeRateValueSingleStatus;
    @Value("${scoring.filters.soft.maritalStatus.married.changeRate}")
    private BigDecimal changeRateValueMarriedStatus;

    //Семейное положение: Замужем/женат → ставка уменьшается на 3;
    //                    Разведен → ставка увеличивается на 1
    @Override
    public BigDecimal rateDelta(ScoringDataDto dto) {
        return switch (dto.getMaritalStatus()) {
            case SINGLE -> changeRateValueSingleStatus;
            case MARRIED -> changeRateValueMarriedStatus;
            default -> BigDecimal.ZERO;
        };
    }

    @Override
    public BigDecimal insuranceDelta(ScoringDataDto dto) {
        return BigDecimal.ZERO;
    }

}