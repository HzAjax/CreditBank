package ru.volodin.CreditBank.service.scoring.filter.soft;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.volodin.CreditBank.entity.dto.RateAndInsuredServiceDto;
import ru.volodin.CreditBank.entity.dto.ScoringDataDto;
import ru.volodin.CreditBank.service.scoring.filter.ScoringLightFilter;
import ru.volodin.CreditBank.service.scoring.filter.ScoringSoftFilter;

import java.math.BigDecimal;

@Component
public class SalaryClientSoftScoringFilter implements ScoringSoftFilter, ScoringLightFilter {

    @Value("${scoring.filters.soft.salaryClient.changeRate}")
    private BigDecimal changeRateValue;

    //Зарплтаный клиент или нет
    @Override
    public RateAndInsuredServiceDto check(ScoringDataDto scoringDataDto) {
        if (scoringDataDto.getIsSalaryClient()) {
            return new RateAndInsuredServiceDto(changeRateValue, BigDecimal.ZERO);
        }
        return new RateAndInsuredServiceDto(BigDecimal.ZERO, BigDecimal.ZERO);
    }

    @Override
    public RateAndInsuredServiceDto check(boolean status) {
        if (status) {
            return new RateAndInsuredServiceDto(changeRateValue, BigDecimal.ZERO);
        }
        return new RateAndInsuredServiceDto(BigDecimal.ZERO, BigDecimal.ZERO);
    }
}