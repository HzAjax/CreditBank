package ru.volodin.deal.entity.dto.api;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;


@Schema(
        description = "Loan offer DTO containing possible loan terms calculated based on the client's request"
)
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoanOfferDto {
    @Schema(
            description = "Unique identifier of the loan statement",
            example = "87c1d63c-040d-4e63-bb9d-b6817bca9103"
    )
    private UUID statementId;

    @Schema(
            description = "The amount initially requested by the client",
            example = "300000"
    )
    private BigDecimal requestedAmount;

    @Schema(
            description = "Total loan amount including insurance (if applicable)",
            example = "506261.16"
    )
    private BigDecimal totalAmount;

    @Schema(
            description = "Loan term in months",
            example = "36"
    )
    private Integer term;

    @Schema(
            description = "Monthly payment amount",
            example = "14062.81"
    )
    private BigDecimal monthlyPayment;

    @Schema(
            description = "Annual interest rate (percentage)",
            example = "16"
    )
    private BigDecimal rate;

    @Schema(
            description = "Indicates whether insurance is enabled for this offer",
            example = "true"
    )
    private Boolean isInsuranceEnabled;

    @Schema(
            description = "Indicates whether the client is a salary client",
            example = "true"
    )
    private Boolean isSalaryClient;

    public LoanOfferDto(UUID statementId, LoanOfferDto loanOffer) {
        this.statementId = statementId;
        this.requestedAmount = loanOffer.getRequestedAmount();
        this.totalAmount = loanOffer.getTotalAmount();
        this.term = loanOffer.getTerm();
        this.monthlyPayment = loanOffer.getMonthlyPayment();
        this.rate = loanOffer.getRate();
        this.isInsuranceEnabled = loanOffer.getIsInsuranceEnabled();
        this.isSalaryClient = loanOffer.getIsSalaryClient();
    }
}
