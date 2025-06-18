package ru.volodin.calculator.entity.dto.api.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(
        description = "Represents a single entry in the loan payment schedule, including breakdown of payment and remaining debt"
)
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentScheduleElementDto {
    @Schema(
            description = "Sequential payment number",
            example = "1"
    )
    private Integer number;

    @Schema(
            description = "Date of the payment",
            example = "2025-07-17"
    )
    private LocalDate date;

    @Schema(
            description = "Total payment for the period (principal + interest)",
            example = "86274.18"
    )
    private BigDecimal totalPayment;

    @Schema(
            description = "Interest portion of the payment",
            example = "5000.00"
    )
    private BigDecimal interestPayment;

    @Schema(
            description = "Principal (debt) portion of the payment",
            example = "81274.18"
    )
    private BigDecimal debtPayment;

    @Schema(
            description = "Remaining debt after this payment",
            example = "418725.82"
    )
    private BigDecimal remainingDebt;
}