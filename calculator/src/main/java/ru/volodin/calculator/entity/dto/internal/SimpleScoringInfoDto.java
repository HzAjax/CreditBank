package ru.volodin.calculator.entity.dto.internal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SimpleScoringInfoDto {
    private boolean isInsurance;
    private boolean isSalaryClient;
    private BigDecimal newRate;
    private BigDecimal insurance;
}

