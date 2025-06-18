package ru.volodin.calculator.service.credit;

import ru.volodin.calculator.entity.dto.api.response.CreditDto;
import ru.volodin.calculator.entity.dto.api.response.LoanOfferDto;
import ru.volodin.calculator.entity.dto.api.request.ScoringDataDto;
import ru.volodin.calculator.entity.dto.internal.SimpleScoringInfoDto;

import java.math.BigDecimal;
import java.util.List;

public interface CreditCalculation {
    List<LoanOfferDto> generateLoanOffer(BigDecimal amount, Integer term, List<SimpleScoringInfoDto> info);
    CreditDto calculate(ScoringDataDto scoringDataDto, BigDecimal newRate, BigDecimal insuredService);
}
