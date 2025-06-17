package ru.volodin.calculator.service.credit.helper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import ru.volodin.calculator.entity.dto.api.response.PaymentScheduleElementDto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.within;

class CalculatorHelperImplTest {

    private CalculatorHelperImpl helper;

    @BeforeEach
    void setUp() {
        helper = new CalculatorHelperImpl();

        ReflectionTestUtils.setField(helper, "countDigitAfterPoint", 2);
    }

    @Test
    void testGetMonthlyRate_shouldDivideBy12And100() {
        BigDecimal rate = new BigDecimal("12");
        BigDecimal result = helper.getMonthlyRate(rate);

        assertThat(result).isEqualByComparingTo("0.01");
    }

    @Test
    void testRound_shouldRoundToConfiguredScale() {
        BigDecimal input = new BigDecimal("123.4567");
        BigDecimal result = helper.round(input);

        assertThat(result).isEqualByComparingTo("123.46");
    }

    @Test
    void testGetMonthlyPayment_shouldReturnCorrectValue() {
        BigDecimal amount = new BigDecimal("100000");
        BigDecimal rate = new BigDecimal("12");
        int term = 12;

        BigDecimal payment = helper.getMonthlyPayment(amount, term, rate);

        BigDecimal approx = new BigDecimal("8884.00");
        assertThat(payment).isNotNull().isCloseTo(approx, within(BigDecimal.valueOf(1.00)));
    }

    @Test
    void testGetSchedule_shouldReturnCorrectSizeAndFields() {
        BigDecimal amount = new BigDecimal("100000");
        BigDecimal rate = new BigDecimal("12");
        int term = 3;

        BigDecimal monthlyRate = helper.getMonthlyRate(rate);
        BigDecimal monthlyPayment = helper.getMonthlyPayment(amount, term, rate);

        List<PaymentScheduleElementDto> schedule = helper.getSchedule(monthlyPayment, monthlyRate, amount, term);

        assertThat(schedule).hasSize(term);

        PaymentScheduleElementDto first = schedule.getFirst();
        assertThat(first.getNumber()).isEqualTo(1);
        assertThat(first.getTotalPayment()).isNotNull();
        assertThat(first.getDate()).isEqualTo(LocalDate.now().plusMonths(1));
    }

    @Test
    void testGetPsk_shouldReturnSumOfInterestAndDebt() {
        List<PaymentScheduleElementDto> schedule = List.of(
                PaymentScheduleElementDto.builder()
                        .interestPayment(new BigDecimal("100"))
                        .debtPayment(new BigDecimal("900"))
                        .build(),
                PaymentScheduleElementDto.builder()
                        .interestPayment(new BigDecimal("90"))
                        .debtPayment(new BigDecimal("910"))
                        .build()
        );

        BigDecimal result = helper.getPsk(schedule);
        assertThat(result).isEqualByComparingTo("2000");
    }
}
