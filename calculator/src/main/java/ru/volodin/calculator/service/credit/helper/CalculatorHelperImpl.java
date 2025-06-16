package ru.volodin.calculator.service.credit.helper;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.volodin.calculator.configuration.ServiceProperties;
import ru.volodin.calculator.entity.dto.api.response.PaymentScheduleElementDto;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CalculatorHelperImpl implements CalculatorHelper{

    private final ServiceProperties serviceProps;

    @Value("${service.calculator.round}")
    private Integer countDigitAfterPoint;

    private static final BigDecimal ONE_HUNDRED = BigDecimal.valueOf(100);
    private static final BigDecimal TWELVE = BigDecimal.valueOf(12);
    private static final MathContext MC = MathContext.DECIMAL128;

    @Override
    public BigDecimal getPsk(List<PaymentScheduleElementDto> schedule) {
        BigDecimal totalInterestPayments = BigDecimal.ZERO;
        for (PaymentScheduleElementDto element : schedule) {
            totalInterestPayments = totalInterestPayments.add(element.getInterestPayment());
        }
        BigDecimal totalDebtPayments = BigDecimal.ZERO;
        for (PaymentScheduleElementDto element : schedule) {
            totalDebtPayments = totalDebtPayments.add(element.getDebtPayment());
        }
        return totalInterestPayments.add(totalDebtPayments);
    }

    @Override
    public List<PaymentScheduleElementDto> getSchedule(BigDecimal monthlyPayment, BigDecimal monthlyRate, BigDecimal totalAmount, Integer term) {
        List<PaymentScheduleElementDto> schedule = new ArrayList<>();
        BigDecimal remainingDebt = totalAmount;

        for (int i = 1; i <= term; i++) {
            LocalDate month = LocalDate.now().plusMonths(i);
            BigDecimal interestPayment = remainingDebt.multiply(monthlyRate);
            BigDecimal debtPayment = monthlyPayment.subtract(interestPayment);
            remainingDebt = remainingDebt.subtract(debtPayment);

            schedule.add(PaymentScheduleElementDto.builder()
                    .number(i)
                    .date(month)
                    .totalPayment(round(monthlyPayment))
                    .interestPayment(round(interestPayment))
                    .debtPayment(round(debtPayment))
                    .remainingDebt(round(remainingDebt))
                    .build());
        }
        return schedule;
    }

    @Override
    public BigDecimal getMonthlyPayment(BigDecimal totalAmount, Integer term, BigDecimal newRate) {
        BigDecimal monthlyPercent = getMonthlyRate(newRate);
        return totalAmount.multiply(
                monthlyPercent.add(
                        monthlyPercent.divide(
                                BigDecimal.ONE.add(monthlyPercent).pow(term).subtract(BigDecimal.ONE), MC
                        )
                )
        );
    }

    @Override
    public BigDecimal getMonthlyRate(BigDecimal newRate) {
        return newRate.divide(ONE_HUNDRED).divide(TWELVE, MC);
    }

    @Override
    public BigDecimal round(BigDecimal value) {
        return value.setScale(countDigitAfterPoint, RoundingMode.HALF_EVEN);
    }
}
