package ru.volodin.CreditBank.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.volodin.CreditBank.entity.dto.*;
import ru.volodin.CreditBank.service.CalculatorService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/calculator")
@Tag(name="Calculator of loans controller", description = "displays possible offers and calculates the loan")
public class CalculatorController {

    private final CalculatorService calculatorService;

    @PostMapping("/offers")
    public List<LoanOfferDto> calculatePossibleLoanTerms(@RequestBody @Valid LoanStatementRequestDto loanStatementRequestDto) {

        log.info("Request: POST /offers");
        log.debug("Request,body={}", loanStatementRequestDto);

        List<LoanOfferDto> loanOfferDto = calculatorService.calculateLoan(loanStatementRequestDto);

        log.debug("Response,body={}", loanOfferDto);
        log.info("Response: POST /offer");

        return loanOfferDto;
    }

    @PostMapping("/calc")
        public CreditDto fullCalculateLoanParametersAndScoring(@RequestBody @Valid ScoringDataDto scoringDataDto) {

        log.info("Request: POST /calc");
        log.debug("Request, body={}", scoringDataDto);

        CreditDto creditDto = calculatorService.calculateCredit(scoringDataDto);

        log.debug("Response, body={}", creditDto);
        log.info("Response: POST /calc");

        return creditDto;
    }

}
