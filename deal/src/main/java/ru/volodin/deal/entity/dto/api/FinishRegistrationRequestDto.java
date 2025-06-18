package ru.volodin.deal.entity.dto.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.volodin.deal.entity.dto.enums.Gender;
import ru.volodin.deal.entity.dto.enums.MaritalStatus;

import java.time.LocalDate;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FinishRegistrationRequestDto {

    private Gender gender;
    private MaritalStatus maritalStatus;
    private Integer dependentAmount;
    private LocalDate passportIssueDate;
    private String passportIssueBrach;
    private EmploymentDto employment;
    private String accountNumber;
}
