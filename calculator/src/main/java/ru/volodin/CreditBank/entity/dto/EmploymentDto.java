package ru.volodin.CreditBank.entity.dto;

import lombok.*;
import ru.volodin.CreditBank.entity.dto.enums.EmploymentStatus;
import ru.volodin.CreditBank.entity.dto.enums.Position;

import java.math.BigDecimal;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmploymentDto {
    private EmploymentStatus employmentStatus;
    private String employerINN;
    private BigDecimal salary;
    private Position position;
    private Integer workExperienceTotal;
    private Integer workExperienceCurrent;
}