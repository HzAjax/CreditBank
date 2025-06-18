package ru.volodin.deal.entity.jsonb;

import lombok.Data;
import ru.volodin.deal.entity.dto.enums.EmploymentStatus;
import ru.volodin.deal.entity.dto.enums.Position;

import java.math.BigDecimal;

@Data
public class Employment {
    private EmploymentStatus status;
    private String employerInn;
    private BigDecimal salary;
    private Position position;
    private Integer workExperienceTotalInMonths;
    private Integer workExperienceCurrentInMonths;
}
