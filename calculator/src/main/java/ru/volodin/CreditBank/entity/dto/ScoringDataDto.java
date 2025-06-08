package ru.volodin.CreditBank.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.volodin.CreditBank.entity.dto.enums.Gender;
import ru.volodin.CreditBank.entity.dto.enums.MaritalStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScoringDataDto {
    private BigDecimal amount;
    private Integer term;
    private String firstName;
    private String lastName;
    private String middleName;
    private Gender gender;
    private LocalDate birthdate;
    private String passportSeries;
    private String passportNumber;
    private LocalDate passportIssueDate;
    private String passportIssueBranch;
    private MaritalStatus maritalStatus;
    private Integer dependentAmount;
    private EmploymentDto employment;
    private String accountNumber;
    private Boolean isInsuranceEnabled;
    private Boolean isSalaryClient;
}
