package ru.volodin.deal.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.volodin.deal.entity.StatementEntity;
import ru.volodin.deal.entity.dto.api.FinishRegistrationRequestDto;
import ru.volodin.deal.entity.dto.api.LoanOfferDto;
import ru.volodin.deal.entity.dto.api.LoanStatementRequestDto;
import ru.volodin.deal.service.DealService;
import ru.volodin.errorhandling.dto.ErrorMessageDto;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/deal")
@RequiredArgsConstructor
@Tag(
        name = "Deal API",
        description = "Операции по заявкам: расчёт предложений, выбор оффера, завершение регистрации и финальный расчёт кредита"
)
public class DealController {

    private final DealService dealService;

    @PostMapping("/statement")
    @Operation(summary = "Calculation possible offers")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Calculate and save credit",
                    content = @Content( mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = LoanOfferDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid format",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorMessageDto.class))}),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content( mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorMessageDto.class)))})
    public List<LoanOfferDto> calculateLoanOffers(@RequestBody @Valid LoanStatementRequestDto loanStatement) {
        return dealService.calculateLoanOffers(loanStatement);
    }

    @PostMapping("offer/select")
    @Operation(summary = "Select one of the offers")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Select offer"),
            @ApiResponse(responseCode = "404", description = "Statement not found",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorMessageDto.class))}),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content( mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorMessageDto.class)))})
    public void selectLoanOffer(@RequestBody LoanOfferDto loanOffer) {
        dealService.selectLoanOffer(loanOffer);
    }

    @PostMapping("/calculate/{statementId}")
    @Operation(summary = "Completion of registration + full credit calculation")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Calculate and save credit"),
            @ApiResponse(responseCode = "404", description = "Statement not found",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorMessageDto.class))}),
            @ApiResponse(responseCode = "422", description = "The request could not be completed",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorMessageDto.class))}),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content( mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorMessageDto.class)))})
    public void calculateCredit(@RequestBody FinishRegistrationRequestDto finishRegistration,
                                @PathVariable @NotNull UUID statementId) {
        dealService.calculateCredit(statementId, finishRegistration);
    }

    @PostMapping("/document/{statementId}/send")
    @Operation(summary = "Request to send documents")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Documents successfully sent"),
            @ApiResponse(responseCode = "404", description = "Statement not found",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorMessageDto.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorMessageDto.class)))
    })
    public void prepareDocuments(@PathVariable UUID statementId) {
        dealService.prepareDocuments(statementId);
    }

    @PostMapping("/document/{statementId}/sign")
    @Operation(summary = "Request to sign documents (generate SES code)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "SES code created and sent"),
            @ApiResponse(responseCode = "404", description = "Statement not found",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorMessageDto.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorMessageDto.class)))
    })
    public void createSignCodeDocuments(@PathVariable UUID statementId) {
        dealService.createSignCodeDocuments(statementId);
    }

    @PostMapping("/document/{statementId}/code")
    @Operation(summary = "Signing documents by SES code")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Documents signed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid SES code",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorMessageDto.class))),
            @ApiResponse(responseCode = "404", description = "Statement not found",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorMessageDto.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorMessageDto.class)))
    })
    public void signCodeDocument(@PathVariable UUID statementId, @RequestParam String sesCode) {
        dealService.signCodeDocument(statementId, sesCode);
    }

    @GetMapping("/admin/statement/{statementId}")
    @Operation(summary = "Get statement by ID",
            description = "Returns a statement by its unique identifier")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Statement successfully found",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = StatementEntity.class))),
            @ApiResponse(responseCode = "404", description = "Statement not found",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorMessageDto.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorMessageDto.class)))
    })
    public StatementEntity getStatementById(@PathVariable UUID statementId) {
        return dealService.getStatementById(statementId);
    }

    @GetMapping("admin/statement")
    @Operation(summary = "Get all statements",
            description = "Retrieves the list of all statements in the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of statements successfully retrieved",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = StatementEntity.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorMessageDto.class)))
    })
    public List<StatementEntity> getAllStatement() {
        return dealService.findAllStatemnt();
    }
}
