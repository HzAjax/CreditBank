package ru.volodin.calculator.entity.dto.api.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Schema(
        description = "DTO containing full credit calculation result including monthly payment, interest rate, total cost, and detailed payment schedule"
)
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreditDto {
    @Schema(
            description = "Approved loan amount",
            example = "500000"
    )
    private BigDecimal amount;

    @Schema(
            description = "Loan term in months",
            example = "6"
    )
    private Integer term;

    @Schema(
            description = "Monthly payment amount",
            example = "86274.18"
    )
    private BigDecimal monthlyPayment;

    @Schema(
            description = "Annual interest rate (percentage)",
            example = "12"
    )
    private BigDecimal rate;

    @Schema(
            description = "Full loan cost percentage (APR)",
            example = "517645.09"
    )
    private BigDecimal psk;

    @Schema(
            description = "Indicates whether insurance is included",
            example = "false"
    )
    private Boolean isInsuranceEnabled;

    @Schema(
            description = "Indicates whether the client is a salary client",
            example = "true"
    )
    private Boolean isSalaryClient;

    @Schema(
            description = "List of monthly payment schedule entries"
    )
    private List<PaymentScheduleElementDto> paymentSchedule;
}
