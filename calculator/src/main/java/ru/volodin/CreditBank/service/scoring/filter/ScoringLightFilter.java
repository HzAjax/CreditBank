package ru.volodin.CreditBank.service.scoring.filter;

import ru.volodin.CreditBank.entity.dto.RateAndInsuredServiceDto;

public interface ScoringLightFilter {
     RateAndInsuredServiceDto check(boolean status);
}
