package ru.volodin.calculator.entity.dto.api.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(
        description = "DTO containing initial loan request data provided by the client"
)
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoanStatementRequestDto {

    @Schema(
            description = "Requested loan amount",
            example = "300000"
    )
    @NotNull(message = "Amount must not be null")
    private BigDecimal amount;

    @Schema(
            description = "Loan term in months (6–60)",
            example = "36"
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
            description = "Client's email address",
            example = "ivan@example.com"
    )
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;

    @Schema(
            description = "Client's birthdate (must be in the past)",
            example = "1990-01-01"
    )
    @NotNull(message = "Birthdate is required")
    @Past(message = "Birthdate must be in the past")
    private LocalDate birthdate;

    @Schema(
            description = "Passport series (4 digits)",
            example = "1234"
    )
    @NotBlank(message = "Passport series is required")
    @Pattern(regexp = "\\d{4}", message = "Passport series must be 4 digits")
    private String passportSeries;

    @Schema(
            description = "Passport number (6 digits)",
            example = "567890"
    )
    @NotBlank(message = "Passport number is required")
    @Pattern(regexp = "\\d{6}", message = "Passport number must be 6 digits")
    private String passportNumber;
}
