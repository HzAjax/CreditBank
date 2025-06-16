package ru.volodin.calculator.service.scoring.filter.soft;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.volodin.calculator.configuration.ScoringFilterProperties;
import ru.volodin.calculator.configuration.ServiceProperties;
import ru.volodin.calculator.entity.dto.api.request.ScoringDataDto;
import ru.volodin.calculator.service.scoring.filter.ScoringSoftFilter;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class InsuranceSoftScoringFilter implements ScoringSoftFilter {

    private final ScoringFilterProperties scoringProps;
    private final ServiceProperties serviceProps;

    @Value("${service.insurance.cost}")
    private BigDecimal costInsurance;
    @Value("${scoring.filters.soft.insurance.changeRate}")
    private BigDecimal changeRateValue;

    //Страхование
    @Override
    public BigDecimal rateDelta(ScoringDataDto dto) {
        return Boolean.TRUE.equals(dto.getIsInsuranceEnabled()) ? changeRateValue : BigDecimal.ZERO;
    }

    @Override
    public BigDecimal insuranceDelta(ScoringDataDto dto) {
        return Boolean.TRUE.equals(dto.getIsInsuranceEnabled()) ? costInsurance : BigDecimal.ZERO;
    }
}
