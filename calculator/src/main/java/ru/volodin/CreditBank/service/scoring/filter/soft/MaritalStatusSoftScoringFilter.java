package ru.volodin.CreditBank.service.scoring.filter.soft;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.volodin.CreditBank.entity.dto.RateAndInsuredServiceDto;
import ru.volodin.CreditBank.entity.dto.ScoringDataDto;
import ru.volodin.CreditBank.service.scoring.filter.ScoringSoftFilter;

import java.math.BigDecimal;

@Component
public class MaritalStatusSoftScoringFilter implements ScoringSoftFilter {

    @Value("${scoring.filters.soft.maritalStatus.single.changeRate}")
    private BigDecimal changeRateValueSingleStatus;
    @Value("${scoring.filters.soft.maritalStatus.married.changeRate}")
    private BigDecimal changeRateValueMarriedStatus;

    //Семейное положение: Замужем/женат → ставка уменьшается на 3;
    //                    Разведен → ставка увеличивается на 1
    @Override
    public RateAndInsuredServiceDto check(ScoringDataDto scoringDataDto) {
        switch (scoringDataDto.getMaritalStatus()) {
            case SINGLE -> {
                return new RateAndInsuredServiceDto(changeRateValueSingleStatus, BigDecimal.ZERO);
            }
            case MARRIED -> {
                return new RateAndInsuredServiceDto(changeRateValueMarriedStatus, BigDecimal.ZERO);
            }
            default -> {
                return new RateAndInsuredServiceDto(BigDecimal.ZERO, BigDecimal.ZERO);
            }
        }
    }

}