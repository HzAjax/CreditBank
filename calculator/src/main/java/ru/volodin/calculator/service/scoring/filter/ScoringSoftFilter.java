package ru.volodin.calculator.service.scoring.filter;

import ru.volodin.calculator.entity.dto.api.ScoringDataDto;

import java.math.BigDecimal;

public interface ScoringSoftFilter {
    BigDecimal rateDelta(ScoringDataDto dto);
    BigDecimal insuranceDelta(ScoringDataDto dto);
}
