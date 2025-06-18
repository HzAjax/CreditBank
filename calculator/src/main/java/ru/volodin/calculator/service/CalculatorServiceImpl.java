package ru.volodin.calculator.service;



import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.volodin.calculator.entity.dto.api.response.CreditDto;
import ru.volodin.calculator.entity.dto.api.response.LoanOfferDto;
import ru.volodin.calculator.entity.dto.api.request.LoanStatementRequestDto;
import ru.volodin.calculator.entity.dto.api.request.ScoringDataDto;
import ru.volodin.calculator.entity.dto.internal.SimpleScoringInfoDto;
import ru.volodin.calculator.service.credit.CreditCalculation;
import ru.volodin.calculator.service.scoring.ScoringProvider;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CalculatorServiceImpl implements CalculatorService {

    private final ScoringProvider scoringProvider;
    private final CreditCalculation creditCalculation;

    @Override
    public List<LoanOfferDto> calculateLoan(LoanStatementRequestDto loanStatementRequestDto) {

        log.debug("Calculation of loans, loanStatementRequestDto={}", loanStatementRequestDto);

        List<SimpleScoringInfoDto> info = scoringProvider.simpleScoring();
        List<LoanOfferDto> loanOffers = creditCalculation.generateLoanOffer(loanStatementRequestDto.getAmount(), loanStatementRequestDto.getTerm(), info)
                .stream()
                .sorted(Comparator.comparing(LoanOfferDto::getRate))
                .toList();

        log.debug("LoansOffers: {}", loanOffers);

        return loanOffers;
    }

    @Override
    public CreditDto calculateCredit(ScoringDataDto scoringDataDto) {

        log.info("Calculation of loans");
        log.debug("Loans, scoringDataDto, {}", scoringDataDto);

        BigDecimal[] scoringResult = scoringProvider.fullScoring(scoringDataDto);
        CreditDto creditDto = creditCalculation.calculate(scoringDataDto, scoringResult[0], scoringResult[1]);

        log.debug("Loans, creditDto, {}", creditDto);

        return creditDto;
    }
}
