package ru.volodin.CreditBank.service.scoring.filter.soft;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.volodin.CreditBank.entity.dto.RateAndInsuredServiceDto;
import ru.volodin.CreditBank.entity.dto.ScoringDataDto;
import ru.volodin.CreditBank.service.scoring.filter.ScoringSoftFilter;

import java.math.BigDecimal;

@Component
public class InsuranceSoftScoringFilter implements ScoringSoftFilter {

    @Value("${service.insurance.cost}")
    private BigDecimal costInsurance;
    @Value("${scoring.filters.soft.insurance.changeRate}")
    private BigDecimal changeRateValue;

    //Страхование
    @Override
    public RateAndInsuredServiceDto check(ScoringDataDto scoringDataDto) {
        if(scoringDataDto.getIsInsuranceEnabled()) {
            return new RateAndInsuredServiceDto(changeRateValue, costInsurance);
        }
        return new RateAndInsuredServiceDto(BigDecimal.ZERO, BigDecimal.ZERO);
    }


}
