package ru.volodin.deal.entity.dto.api;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.volodin.deal.entity.dto.enums.Gender;
import ru.volodin.deal.entity.dto.enums.MaritalStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(
        description = "DTO containing detailed client data for credit scoring, including personal, passport, employment, and loan parameters"
)
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ScoringDataDto {

    @Schema(
            description = "Requested loan amount",
            example = "500000"
    )
    @NotNull(message = "Amount must not be null")
    private BigDecimal amount;

    @Schema(
            description = "Loan term in months (6–60)",
            example = "6"
    )
    @NotNull(message = "Term must not be null")
    @Min(value = 6, message = "Minimum term is 6 months")
    @Max(value = 60, message = "Maximum term is 60 months")
    private Integer term;

    @Schema(
            description = "Client's first name",
            example = "Ivan"
    )
    @NotBlank(message = "First name is required")
    private String firstName;

    @Schema(
            description = "Client's last name",
            example = "Ivanov"
    )
    @NotBlank(message = "Last name is required")
    private String lastName;

    @Schema(
            description = "Client's middle name (optional)",
            example = "Ivanovich"
    )
    private String middleName;

    @Schema(
            description = "Client's gender (MALE, FEMALE, OTHER)",
            example = "MALE"
    )
    @NotNull(message = "Gender must be specified")
    private Gender gender;

    @Schema(
            description = "Client's email",
            example = "ivan@mail.ru"
    )
    @NotBlank(message = "Email is required")
    private String email;

    @Schema(
            description = "Client's birthdate",
            example = "1990-01-01"
    )
    @NotNull(message = "Birthdate is required")
    private LocalDate birthdate;

    @Schema(
            description = "Passport series (4 digits)",
            example = "1234"
    )
    @NotBlank(message = "Passport series is required")
    private String passportSeries;

    @Schema(
            description = "Passport number (6 digits)",
            example = "567890"
    )
    @NotBlank(message = "Passport number is required")
    private String passportNumber;

    @Schema(
            description = "Date of passport issue",
            example = "2010-06-15"
    )
    @NotNull(message = "Passport issue date is required")
    private LocalDate passportIssueDate;

    @Schema(
            description = "Name of the passport issuing authority",
            example = "Department of Internal Affairs No. 1"
    )
    @NotBlank(message = "Passport issue branch is required")
    private String passportIssueBranch;

    @Schema(
            description = "Client's marital status (SINGLE, MARRIED, DIVORCED)",
            example = "MARRIED"
    )
    @NotNull(message = "Marital status must be specified")
    private MaritalStatus maritalStatus;

    @Schema(
            description = "Number of dependents",
            example = "1"
    )
    @NotNull(message = "Dependent amount must be specified")
    @Min(value = 0, message = "Dependent amount cannot be negative")
    private Integer dependentAmount;

    @Schema(description = "Client's employment details")
    @NotNull(message = "Employment must be provided")
    @Valid
    private EmploymentDto employment;

    @Schema(
            description = "Client's bank account number",
            example = "40817810099910004312"
    )
    @NotBlank(message = "Account number is required")
    private String accountNumber;

    @Schema(
            description = "Whether the client has opted for insurance",
            example = "false"
    )
    @NotNull(message = "Insurance flag must be provided")
    private Boolean isInsuranceEnabled;

    @Schema(
            description = "Whether the client is a salary client",
            example = "true"
    )
    @NotNull(message = "Salary client flag must be provided")
    private Boolean isSalaryClient;
}
