package ru.volodin.deal.client;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;
import ru.volodin.deal.entity.dto.api.CreditDto;
import ru.volodin.deal.entity.dto.api.LoanOfferDto;
import ru.volodin.deal.entity.dto.api.LoanStatementRequestDto;
import ru.volodin.deal.entity.dto.api.ScoringDataDto;

import java.util.List;

@HttpExchange
public interface CalculatorHttpClient {

    @PostExchange("${client.calculator.path.offers}")
    List<LoanOfferDto> calculateLoanOffers(@RequestBody LoanStatementRequestDto requestDto);

    @PostExchange("${client.calculator.path.calc}")
    CreditDto getCredit(@RequestBody ScoringDataDto scoringDataDto);
}
