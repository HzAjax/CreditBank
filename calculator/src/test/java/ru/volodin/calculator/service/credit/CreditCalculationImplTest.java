package ru.volodin.calculator.service.credit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import ru.volodin.calculator.entity.dto.api.request.ScoringDataDto;
import ru.volodin.calculator.entity.dto.api.response.CreditDto;
import ru.volodin.calculator.entity.dto.api.response.LoanOfferDto;
import ru.volodin.calculator.entity.dto.api.response.PaymentScheduleElementDto;
import ru.volodin.calculator.entity.dto.internal.SimpleScoringInfoDto;
import ru.volodin.calculator.service.credit.helper.CalculatorHelper;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class CreditCalculationImplTest {

    @Mock
    private CalculatorHelper calculatorHelper;

    @InjectMocks
    private CreditCalculationImpl creditCalculation;

    @BeforeEach
    void init() {
        ReflectionTestUtils.setField(creditCalculation, "countDigitAfterPoint", 2);
    }

    @Test
    void testGenerateLoanOffer_shouldReturnLoanOfferList_andVerifyCalls() {
        // arrange
        BigDecimal amount = new BigDecimal("100000");
        int term = 12;
        BigDecimal rate = new BigDecimal("10");
        BigDecimal insurance = new BigDecimal("7000");
        BigDecimal totalAmount = amount.add(insurance);
        BigDecimal monthlyRate = new BigDecimal("0.0083");
        BigDecimal monthlyPayment = new BigDecimal("8900.00");
        BigDecimal psk = new BigDecimal("107000");

        SimpleScoringInfoDto scoringInfo = new SimpleScoringInfoDto(true, true, rate, insurance);
        List<SimpleScoringInfoDto> listInfo = List.of(scoringInfo);

        List<PaymentScheduleElementDto> schedule = List.of(mock(PaymentScheduleElementDto.class));

        when(calculatorHelper.getMonthlyRate(rate)).thenReturn(monthlyRate);
        when(calculatorHelper.getMonthlyPayment(totalAmount, term, rate)).thenReturn(monthlyPayment);
        when(calculatorHelper.getSchedule(monthlyPayment, monthlyRate, totalAmount, term)).thenReturn(schedule);
        when(calculatorHelper.getPsk(schedule)).thenReturn(psk);

        // act
        List<LoanOfferDto> result = creditCalculation.generateLoanOffer(amount, term, listInfo);

        // assert
        assertThat(result).hasSize(1);
        LoanOfferDto offer = result.get(0);
        assertThat(offer.getRequestedAmount()).isEqualByComparingTo(amount);
        assertThat(offer.getTotalAmount()).isEqualByComparingTo(psk);
        assertThat(offer.getMonthlyPayment()).isEqualByComparingTo(monthlyPayment.setScale(2));
        assertThat(offer.getRate()).isEqualByComparingTo(rate);
        assertThat(offer.getIsInsuranceEnabled()).isTrue();
        assertThat(offer.getIsSalaryClient()).isTrue();

        // verify
        verify(calculatorHelper).getMonthlyRate(rate);
        verify(calculatorHelper).getMonthlyPayment(totalAmount, term, rate);
        verify(calculatorHelper).getSchedule(monthlyPayment, monthlyRate, totalAmount, term);
        verify(calculatorHelper).getPsk(schedule);
        verifyNoMoreInteractions(calculatorHelper);
    }

    @Test
    void testCalculate_shouldReturnCreditDto_andVerifyCalls() {
        // arrange
        ScoringDataDto dto = ScoringDataDto.builder()
                .amount(new BigDecimal("100000"))
                .term(12)
                .isInsuranceEnabled(true)
                .isSalaryClient(false)
                .build();

        BigDecimal newRate = new BigDecimal("12");
        BigDecimal insurance = new BigDecimal("7000");
        BigDecimal totalAmount = dto.getAmount().add(insurance);
        BigDecimal monthlyRate = new BigDecimal("0.01");
        BigDecimal monthlyPayment = new BigDecimal("8900.1234");
        BigDecimal psk = new BigDecimal("107000.5678");

        List<PaymentScheduleElementDto> schedule = List.of(
                PaymentScheduleElementDto.builder()
                        .number(1)
                        .date(LocalDate.now().plusMonths(1))
                        .totalPayment(monthlyPayment)
                        .interestPayment(BigDecimal.TEN)
                        .debtPayment(BigDecimal.valueOf(8890.12))
                        .remainingDebt(BigDecimal.valueOf(91109.88))
                        .build()
        );

        when(calculatorHelper.getMonthlyRate(newRate)).thenReturn(monthlyRate);
        when(calculatorHelper.getMonthlyPayment(totalAmount, dto.getTerm(), newRate)).thenReturn(monthlyPayment);
        when(calculatorHelper.getSchedule(monthlyPayment, monthlyRate, totalAmount, dto.getTerm())).thenReturn(schedule);
        when(calculatorHelper.getPsk(schedule)).thenReturn(psk);
        when(calculatorHelper.round(monthlyPayment)).thenReturn(monthlyPayment.setScale(2, RoundingMode.HALF_EVEN));
        when(calculatorHelper.round(psk)).thenReturn(psk.setScale(2, RoundingMode.HALF_EVEN));

        // act
        CreditDto result = creditCalculation.calculate(dto, newRate, insurance);

        // assert
        assertThat(result).isNotNull();
        assertThat(result.getAmount()).isEqualByComparingTo("100000");
        assertThat(result.getTerm()).isEqualTo(12);
        assertThat(result.getMonthlyPayment()).isEqualByComparingTo("8900.12");
        assertThat(result.getRate()).isEqualByComparingTo("12");
        assertThat(result.getPsk()).isEqualByComparingTo("107000.57");
        assertThat(result.getIsInsuranceEnabled()).isTrue();
        assertThat(result.getIsSalaryClient()).isFalse();
        assertThat(result.getPaymentSchedule()).hasSize(1);

        // verify
        verify(calculatorHelper).getMonthlyRate(newRate);
        verify(calculatorHelper).getMonthlyPayment(totalAmount, dto.getTerm(), newRate);
        verify(calculatorHelper).getSchedule(monthlyPayment, monthlyRate, totalAmount, dto.getTerm());
        verify(calculatorHelper).getPsk(schedule);
        verify(calculatorHelper).round(monthlyPayment);
        verify(calculatorHelper).round(psk);
        verifyNoMoreInteractions(calculatorHelper);
    }
}
