package ru.volodin.CreditBank.service.credit;

import ru.volodin.CreditBank.entity.dto.CreditDto;
import ru.volodin.CreditBank.entity.dto.LoanOfferDto;
import ru.volodin.CreditBank.entity.dto.ScoringDataDto;
import ru.volodin.CreditBank.entity.dto.SimpleScoringInfoDto;

import java.math.BigDecimal;
import java.util.List;

public interface CreditCalculation {
    List<LoanOfferDto> generateLoanOffer(BigDecimal amount, Integer term, List<SimpleScoringInfoDto> info);
    CreditDto calculate(ScoringDataDto scoringDataDto, BigDecimal newRate, BigDecimal insuredService);
}
