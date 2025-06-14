package ru.volodin.calculator.service.scoring.filter.soft;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.volodin.calculator.configuration.ScoringFilterProperties;
import ru.volodin.calculator.entity.dto.api.ScoringDataDto;
import ru.volodin.calculator.service.scoring.filter.ScoringSoftFilter;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class SalaryClientSoftScoringFilter implements ScoringSoftFilter {

    private final ScoringFilterProperties scoringProps;

    @Value("${scoring.filters.soft.salaryClient.changeRate}")
    private BigDecimal changeRateValue;

    //Зарплтаный клиент или нет
    @Override
    public BigDecimal rateDelta(ScoringDataDto dto) {
        return Boolean.TRUE.equals(dto.getIsSalaryClient()) ? changeRateValue : BigDecimal.ZERO;
    }

    @Override
    public BigDecimal insuranceDelta(ScoringDataDto dto) {
        return BigDecimal.ZERO;
    }
}