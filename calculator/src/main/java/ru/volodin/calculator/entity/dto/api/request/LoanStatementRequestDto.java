package ru.volodin.calculator.entity.dto.api.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoanStatementRequestDto {
    @NotNull(message = "Amount must not be null")
    @DecimalMin(value = "10000", message = "Amount must be at least 10,000")
    private BigDecimal amount;

    @NotNull(message = "Term must not be null")
    @Min(value = 6, message = "Minimum term is 6 months")
    @Max(value = 60, message = "Maximum term is 60 months")
    private Integer term;

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    private String middleName; // опционально

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;

    @NotNull(message = "Birthdate is required")
    @Past(message = "Birthdate must be in the past")
    private LocalDate birthdate;

    @NotBlank(message = "Passport series is required")
    @Pattern(regexp = "\\d{4}", message = "Passport series must be 4 digits")
    private String passportSeries;

    @NotBlank(message = "Passport number is required")
    @Pattern(regexp = "\\d{6}", message = "Passport number must be 6 digits")
    private String passportNumber;
}
