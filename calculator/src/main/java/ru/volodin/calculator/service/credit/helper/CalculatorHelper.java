package ru.volodin.calculator.service.credit.helper;

import ru.volodin.calculator.entity.dto.api.response.PaymentScheduleElementDto;

import java.math.BigDecimal;
import java.util.List;

public interface CalculatorHelper {

    BigDecimal getPsk(List<PaymentScheduleElementDto> schedule);
    List<PaymentScheduleElementDto> getSchedule(BigDecimal monthlyPayment
                                                ,BigDecimal monthlyRate
                                                ,BigDecimal totalAmount
                                                ,Integer term);
    BigDecimal getMonthlyPayment(BigDecimal totalAmount, Integer term, BigDecimal newRate);
    BigDecimal getMonthlyRate(BigDecimal newRate);
    BigDecimal round(BigDecimal value);
}
