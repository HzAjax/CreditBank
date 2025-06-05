package ru.volodin.CreditBank.entity.dto;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SimpleScoringInfoDto {
    private OfferCombination offerCombination;
    private RateAndInsuredServiceDto RateAndInsuredServiceDto;
}

