package ru.volodin.calculator.service.credit.helper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.volodin.calculator.entity.dto.api.response.PaymentScheduleElementDto;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class CalculatorHelperImplTest {

    @Autowired
    private CalculatorHelper calculatorHelper;

    @Test
    void getMonthlyRate_shouldReturnCorrectRate() {
        BigDecimal rate = calculatorHelper.getMonthlyRate(new BigDecimal("21"));
        assertThat(rate).isEqualByComparingTo("0.0175");
    }

    @Test
    void getMonthlyPayment_shouldCalculateCorrectly() {
        BigDecimal totalAmount = new BigDecimal("300000");
        int term = 12;
        BigDecimal rate = new BigDecimal("21");

        BigDecimal monthlyPayment = calculatorHelper.getMonthlyPayment(totalAmount, term, rate);

        assertThat(monthlyPayment).isGreaterThan(BigDecimal.ZERO);
    }

    @Test
    void getSchedule_shouldReturnFullList() {
        BigDecimal totalAmount = new BigDecimal("300000");
        int term = 12;
        BigDecimal rate = new BigDecimal("21");

        BigDecimal monthlyRate = calculatorHelper.getMonthlyRate(rate);
        BigDecimal monthlyPayment = calculatorHelper.getMonthlyPayment(totalAmount, term, rate);

        List<PaymentScheduleElementDto> schedule = calculatorHelper.getSchedule(monthlyPayment, monthlyRate, totalAmount, term);

        assertThat(schedule).hasSize(12);
        assertThat(schedule.get(0).getTotalPayment()).isNotNull();
        assertThat(schedule.get(11).getRemainingDebt()).isLessThan(new BigDecimal("5"));
    }

    @Test
    void getPsk_shouldReturnSumOfInterestAndDebt() {
        BigDecimal totalAmount = new BigDecimal("300000");
        int term = 12;
        BigDecimal rate = new BigDecimal("21");

        BigDecimal monthlyRate = calculatorHelper.getMonthlyRate(rate);
        BigDecimal monthlyPayment = calculatorHelper.getMonthlyPayment(totalAmount, term, rate);
        List<PaymentScheduleElementDto> schedule = calculatorHelper.getSchedule(monthlyPayment, monthlyRate, totalAmount, term);

        BigDecimal psk = calculatorHelper.getPsk(schedule);

        assertThat(psk).isGreaterThan(totalAmount);
    }

    @Test
    void round_shouldRespectConfiguredScale() {
        BigDecimal value = new BigDecimal("123.45678");
        BigDecimal rounded = calculatorHelper.round(value);

        assertThat(rounded.scale()).isEqualTo(2);
    }
}
