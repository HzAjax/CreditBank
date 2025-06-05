package ru.volodin.CreditBank.service;

import org.springframework.stereotype.Service;
import ru.volodin.CreditBank.entity.dto.CreditDto;
import ru.volodin.CreditBank.entity.dto.LoanOfferDto;
import ru.volodin.CreditBank.entity.dto.LoanStatementRequestDto;
import ru.volodin.CreditBank.entity.dto.ScoringDataDto;

import java.util.List;

@Service
public interface CalculatorService {
    CreditDto calculateCredit(ScoringDataDto scoringDataDto);

    List<LoanOfferDto> calculateLoan(LoanStatementRequestDto loanStatementRequestDto);
}
