package ru.volodin.calculator.entity.dto.api.request;

import jakarta.validation.constraints.*;
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

    @NotNull(message = "Employment status must not be null")
    @ValidWorkStatus
    private EmploymentStatus employmentStatus;

    @NotBlank(message = "Employer INN is required")
    @Pattern(regexp = "\\d{10}", message = "Employer INN must be exactly 10 digits")
    private String employerINN;

    @NotNull(message = "Salary must not be null")
    @DecimalMin(value = "1000", message = "Salary must be at least 1,000")
    private BigDecimal salary;

    @NotNull(message = "Position must be specified")
    private Position position;

    @NotNull(message = "Total work experience must not be null")
    @ValidExperienceTotal
    private Integer workExperienceTotal;

    @NotNull(message = "Current work experience must not be null")
    @ValidExperienceCurrent
    private Integer workExperienceCurrent;
}