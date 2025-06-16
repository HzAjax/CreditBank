package ru.volodin.calculator.entity.dto.api.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoanOfferDto {
    @Schema(example = "10286ab9-9a03-4a9b-bdc4-202ac201aabe")
    private UUID statementId;
    @Schema(example = "300000")
    private BigDecimal requestedAmount; //запрос
    @Schema(example = "418872.66")
    private BigDecimal totalAmount; //долг + страховка
    @Schema(example = "6")
    private Integer term; //срок
    @Schema(example = "69812.11")
    private BigDecimal monthlyPayment; //платеж в месяц
    @Schema(example = "16")
    private BigDecimal rate; //ставка
    @Schema(example = "true")
    private Boolean isInsuranceEnabled;
    @Schema(example = "true")
    private Boolean isSalaryClient;

}
