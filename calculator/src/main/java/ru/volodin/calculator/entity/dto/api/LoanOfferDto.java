package ru.volodin.calculator.entity.dto.api;

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
    private UUID statementId;
    private BigDecimal requestedAmount; //запрос
    private BigDecimal totalAmount; //долг + страховка
    private Integer term; //срок
    private BigDecimal monthlyPayment; //платеж в месяц
    private BigDecimal rate; //ставка
    private Boolean isInsuranceEnabled;
    private Boolean isSalaryClient;

}
