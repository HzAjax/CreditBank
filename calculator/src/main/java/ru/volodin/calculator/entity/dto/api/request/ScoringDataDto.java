package ru.volodin.calculator.entity.dto.api.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.volodin.calculator.validation.age.ValidAge;
import ru.volodin.calculator.validation.amount.ValidAmount;
import ru.volodin.calculator.entity.dto.enums.Gender;
import ru.volodin.calculator.entity.dto.enums.MaritalStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ValidAmount
public class ScoringDataDto {
    @NotNull(message = "Amount must not be null")
    private BigDecimal amount;

    @NotNull(message = "Term must not be null")
    @Min(value = 6, message = "Minimum term is 6 months")
    @Max(value = 60, message = "Maximum term is 60 months")
    private Integer term;

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    private String middleName;

    @NotNull(message = "Gender must be specified")
    private Gender gender;

    @NotNull(message = "Birthdate is required")
    @ValidAge
    private LocalDate birthdate;

    @NotBlank(message = "Passport series is required")
    private String passportSeries;

    @NotBlank(message = "Passport number is required")
    private String passportNumber;

    @NotNull(message = "Passport issue date is required")
    private LocalDate passportIssueDate;

    @NotBlank(message = "Passport issue branch is required")
    private String passportIssueBranch;

    @NotNull(message = "Marital status must be specified")
    private MaritalStatus maritalStatus;

    @NotNull(message = "Dependent amount must be specified")
    @Min(value = 0, message = "Dependent amount cannot be negative")
    private Integer dependentAmount;

    @NotNull(message = "Employment must be provided")
    @Valid
    private EmploymentDto employment;

    @NotBlank(message = "Account number is required")
    private String accountNumber;

    @NotNull(message = "Insurance flag must be provided")
    private Boolean isInsuranceEnabled;

    @NotNull(message = "Salary client flag must be provided")
    private Boolean isSalaryClient;
}
