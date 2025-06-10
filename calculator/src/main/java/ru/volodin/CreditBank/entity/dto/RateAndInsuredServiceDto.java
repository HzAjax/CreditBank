package ru.volodin.CreditBank.entity.dto;

import lombok.*;

import java.math.BigDecimal;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RateAndInsuredServiceDto {
    private BigDecimal newRate; //новая ставка
    private BigDecimal insuredService; //стоимость страховки
}
