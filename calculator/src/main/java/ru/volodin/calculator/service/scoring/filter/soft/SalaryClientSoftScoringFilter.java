package ru.volodin.calculator.service.scoring.filter.soft;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.volodin.calculator.entity.dto.api.request.ScoringDataDto;
import ru.volodin.calculator.service.scoring.filter.ScoringSoftFilter;

import java.math.BigDecimal;

@Service
public class SalaryClientSoftScoringFilter implements ScoringSoftFilter {

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