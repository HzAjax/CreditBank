package ru.volodin.calculator.controller;

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
import ru.volodin.calculator.entity.dto.api.request.LoanStatementRequestDto;
import ru.volodin.calculator.entity.dto.api.request.ScoringDataDto;
import ru.volodin.calculator.entity.dto.api.response.CreditDto;
import ru.volodin.calculator.entity.dto.api.response.LoanOfferDto;
import ru.volodin.calculator.service.CalculatorService;
import ru.volodin.errorhandling.dto.ErrorMessageDto;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/calculator")
@Tag(name="Calculator of loans controller", description = "displays possible offers and calculates the loan")
public class CalculatorController {

    private final CalculatorService calculatorService;

    @PostMapping("/offers")
    @Operation(summary = "calculation possible offers")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully calculated loan offers",
                    content = @Content( mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = LoanOfferDto.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request - validation failed",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorMessageDto.class))),
            @ApiResponse(responseCode = "422", description = "Unprocessable Entity - business rule violation",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorMessageDto.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorMessageDto.class)))
    })
    public List<LoanOfferDto> calculatePossibleLoanTerms(@RequestBody @Valid LoanStatementRequestDto loanStatementRequestDto) {
        return calculatorService.calculateLoan(loanStatementRequestDto);
    }

    @PostMapping("/calc")
    @Operation(summary = "calculation credit")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully calculated full credit info",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = CreditDto.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request - validation failed",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorMessageDto.class))),
            @ApiResponse(responseCode = "422", description = "Unprocessable Entity - business rule violation",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorMessageDto.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorMessageDto.class)))
    })
    public CreditDto fullCalculateLoanParametersAndScoring(@RequestBody @Valid ScoringDataDto scoringDataDto) {
        return calculatorService.calculateCredit(scoringDataDto);
    }

}