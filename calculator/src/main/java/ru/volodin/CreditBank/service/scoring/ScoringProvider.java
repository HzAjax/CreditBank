package ru.volodin.CreditBank.service.scoring;

import ru.volodin.CreditBank.entity.dto.*;

import java.math.BigDecimal;
import java.util.List;

public interface ScoringProvider {

    RateAndInsuredServiceDto fullScoring(ScoringDataDto scoringDataDto);

    void hardScoring(ScoringDataDto scoringDataDto);

    RateAndInsuredServiceDto softScoring(ScoringDataDto scoringDataDto, BigDecimal rate);

    List<SimpleScoringInfoDto> simpleScoring();
}
