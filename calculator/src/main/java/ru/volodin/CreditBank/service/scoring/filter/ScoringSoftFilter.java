package ru.volodin.CreditBank.service.scoring.filter;

import ru.volodin.CreditBank.entity.dto.RateAndInsuredServiceDto;
import ru.volodin.CreditBank.entity.dto.ScoringDataDto;

public interface ScoringSoftFilter {
    RateAndInsuredServiceDto check(ScoringDataDto scoringDataDto);
}
