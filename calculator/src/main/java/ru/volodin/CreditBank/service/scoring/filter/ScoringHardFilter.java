package ru.volodin.CreditBank.service.scoring.filter;

import ru.volodin.CreditBank.entity.dto.ScoringDataDto;

public interface ScoringHardFilter {
    boolean check(ScoringDataDto scoringDataDto);
}
