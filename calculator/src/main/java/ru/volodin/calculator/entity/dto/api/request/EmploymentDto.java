package ru.volodin.calculator.entity.dto.api.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.volodin.calculator.validation.experience.current.ValidExperienceCurrent;
import ru.volodin.calculator.validation.experience.total.ValidExperienceTotal;
import ru.volodin.calculator.validation.status.ValidWorkStatus;
import ru.volodin.calculator.entity.dto.enums.EmploymentStatus;
import ru.volodin.calculator.entity.dto.enums.Position;

import java.math.BigDecimal;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmploymentDto {
    @ValidWorkStatus
    private EmploymentStatus employmentStatus;
    private String employerINN;
    private BigDecimal salary;
    private Position position;
    @ValidExperienceTotal
    private Integer workExperienceTotal;
    @ValidExperienceCurrent
    private Integer workExperienceCurrent;
}