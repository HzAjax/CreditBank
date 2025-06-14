package ru.volodin.calculator.service;

import ru.volodin.calculator.entity.dto.api.CreditDto;
import ru.volodin.calculator.entity.dto.api.LoanOfferDto;
import ru.volodin.calculator.entity.dto.api.LoanStatementRequestDto;
import ru.volodin.calculator.entity.dto.api.ScoringDataDto;

import java.util.List;

public interface CalculatorService {
    CreditDto calculateCredit(ScoringDataDto scoringDataDto);

    List<LoanOfferDto> calculateLoan(LoanStatementRequestDto loanStatementRequestDto);
}
