package ru.volodin.deal.entity.dto.api;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.volodin.deal.entity.dto.enums.Gender;
import ru.volodin.deal.entity.dto.enums.MaritalStatus;

import java.time.LocalDate;

@Schema(
        description = "DTO for completing client registration, including personal data, passport details, and employment information."
)
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FinishRegistrationRequestDto {
    @Schema(
            description = "Client's gender",
            example = "MALE"
    )
    private Gender gender;

    @Schema(
            description = "Marital status",
            example = "SINGLE"
    )
    private MaritalStatus maritalStatus;

    @Schema(
            description = "Number of dependents",
            example = "0"
    )
    private Integer dependentAmount;

    @Schema(
            description = "Passport issue date (YYYY-MM-DD)",
            example = "2018-05-21"
    )
    private LocalDate passportIssueDate;

    @Schema(
            description = "Passport issuing authority (branch name)",
            example = "Department of Internal Affairs No. 1234"
    )
    private String passportIssueBranch;

    @Schema(description = "Client's employment details")
    private EmploymentDto employment;

    @Schema(
            description = "Bank account number for fund transfer",
            example = "40817810099910004312"
    )
    private String accountNumber;
}
