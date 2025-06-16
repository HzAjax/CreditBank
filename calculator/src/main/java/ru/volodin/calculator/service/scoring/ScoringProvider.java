package ru.volodin.calculator.service.scoring;

import ru.volodin.calculator.entity.dto.api.request.ScoringDataDto;
import ru.volodin.calculator.entity.dto.internal.SimpleScoringInfoDto;

import java.math.BigDecimal;
import java.util.List;

public interface ScoringProvider {

    BigDecimal[] fullScoring(ScoringDataDto scoringDataDto);

    BigDecimal[] softScoring(ScoringDataDto scoringDataDto, BigDecimal rate);

    List<SimpleScoringInfoDto> simpleScoring();
}
