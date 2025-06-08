package ru.volodin.CreditBank.service;

import org.springframework.stereotype.Service;
import ru.volodin.CreditBank.entity.dto.CreditDto;
import ru.volodin.CreditBank.entity.dto.LoanOfferDto;
import ru.volodin.CreditBank.entity.dto.LoanStatementRequestDto;
import ru.volodin.CreditBank.entity.dto.ScoringDataDto;

import java.util.List;

@Service
public class CalculatorServiceImpl implements CalculatorService {
    @Override
    public CreditDto calculateCredit(ScoringDataDto scoringDataDto) {
        return null;
    }

    @Override
    public List<LoanOfferDto> calculateLoan(LoanStatementRequestDto loanStatementRequestDto) {
        return List.of();
    }
}
