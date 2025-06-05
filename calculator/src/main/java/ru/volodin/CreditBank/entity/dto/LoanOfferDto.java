package ru.volodin.CreditBank.entity.dto;

import lombok.*;

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
