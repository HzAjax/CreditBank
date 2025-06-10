package ru.volodin.CreditBank.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.volodin.CreditBank.entity.dto.*;
import ru.volodin.CreditBank.service.scoring.ScoringProvider;
import ru.volodin.CreditBank.service.credit.CreditCalculation;

import java.util.List;
import java.util.stream.Collectors;

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
                .sorted((offer1, offer2) -> offer1.getRate().subtract(offer2.getRate()).intValue())
                .collect(Collectors.toList());

        log.debug("LoansOffers: {}", loanOffers);

        return loanOffers;
    }

    @Override
    public CreditDto calculateCredit(ScoringDataDto scoringDataDto) {

        log.info("Calculation of loans");
        log.debug("Loans, scoringDataDto, {}", scoringDataDto);

        RateAndInsuredServiceDto resultScoring = scoringProvider.fullScoring(scoringDataDto);
        CreditDto creditDto = creditCalculation.calculate(scoringDataDto, resultScoring.getNewRate(), resultScoring.getInsuredService());

        log.debug("Loans, creditDto, {}", creditDto);

        return creditDto;
    }
}
