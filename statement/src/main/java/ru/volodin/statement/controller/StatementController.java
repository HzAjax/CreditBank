package ru.volodin.statement.controller;

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
import ru.volodin.statement.entity.dto.ErrorMessageDto;
import ru.volodin.statement.entity.dto.LoanOfferDto;
import ru.volodin.statement.entity.dto.LoanStatementRequestDto;
import ru.volodin.statement.service.StatementService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/statement")
@RequiredArgsConstructor
@Tag(
        name = "Statement API",
        description = "Операции по заявкам: расчёт предложений, выбор оффера."
)
public class StatementController {

    private final StatementService statementService;

    @PostMapping
    @Operation(summary = "Calculate possible loan offers")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Calculate and save credit",
            content = @Content( mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = LoanOfferDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid format",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorMessageDto.class))}),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content( mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorMessageDto.class)))})
    public List<LoanOfferDto> calculateLoanOffers(@RequestBody @Valid LoanStatementRequestDto loanStatement) {

        return statementService.getLoanOffers(loanStatement);
    }

    @PostMapping("/offer")
    @Operation(summary = "Select one of the offers")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Select offer"),
            @ApiResponse(responseCode = "404", description = "Statement not found",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorMessageDto.class))}),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content( mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorMessageDto.class)))})
    public void selectLoanOffer(@RequestBody LoanOfferDto loanOffer) {

        statementService.selectLoanOffer(loanOffer);
    }
}
