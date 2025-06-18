package ru.volodin.calculator.service;

import ru.volodin.calculator.entity.dto.api.response.CreditDto;
import ru.volodin.calculator.entity.dto.api.response.LoanOfferDto;
import ru.volodin.calculator.entity.dto.api.request.LoanStatementRequestDto;
import ru.volodin.calculator.entity.dto.api.request.ScoringDataDto;

import java.util.List;

public interface CalculatorService {
    CreditDto calculateCredit(ScoringDataDto scoringDataDto);

    List<LoanOfferDto> calculateLoan(LoanStatementRequestDto loanStatementRequestDto);
}
