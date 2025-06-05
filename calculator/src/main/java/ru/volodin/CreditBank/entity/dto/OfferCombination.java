package ru.volodin.CreditBank.entity.dto;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OfferCombination {
    private boolean isInsurance;
    private boolean isSalaryClient;
}
