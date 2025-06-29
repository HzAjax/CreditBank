package ru.volodin.deal.entity.jsonb;

import lombok.*;
import ru.volodin.deal.entity.dto.enums.EmploymentStatus;
import ru.volodin.deal.entity.dto.enums.Position;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Employment {
    private UUID employmentUUID;
    private EmploymentStatus status;
    private String employerInn;
    private BigDecimal salary;
    private Position position;
    private Integer workExperienceTotalInMonths;
    private Integer workExperienceCurrentInMonths;
}
