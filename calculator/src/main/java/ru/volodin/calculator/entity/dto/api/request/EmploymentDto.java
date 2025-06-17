package ru.volodin.calculator.entity.dto.api.request;

import io.swagger.v3.oas.annotations.media.Schema;
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

@Schema(
        description = "DTO containing client's employment details including status, salary, and work experience"
)
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmploymentDto {

    @Schema(
            description = "Client's employment status (UNEMPLOYED, EMPLOYED, SELF_EMPLOYED, BUSINESS_OWNER, UNKNOWN)",
            example = "BUSINESS_OWNER"
    )
    @NotNull(message = "Employment status must not be null")
    @ValidWorkStatus
    private EmploymentStatus employmentStatus;

    @Schema(
            description = "Employer's tax ID (INN), exactly 10 digits",
            example = "7701234567"
    )
    @NotBlank(message = "Employer INN is required")
    @Pattern(regexp = "\\d{10}", message = "Employer INN must be exactly 10 digits")
    private String employerINN;

    @Schema(
            description = "Monthly salary",
            example = "120000"
    )
    @NotNull(message = "Salary must not be null")
    private BigDecimal salary;

    @Schema(
            description = "Client's job position (MANAGER, MID_MANAGER ,TOP_MANAGER)",
            example = "MID_MANAGER"
    )
    @NotNull(message = "Position must be specified")
    private Position position;

    @Schema(
            description = "Total work experience in months",
            example = "60"
    )
    @NotNull(message = "Total work experience must not be null")
    @ValidExperienceTotal
    private Integer workExperienceTotal;

    @Schema(
            description = "Work experience at the current job in months",
            example = "24"
    )
    @NotNull(message = "Current work experience must not be null")
    @ValidExperienceCurrent
    private Integer workExperienceCurrent;
}