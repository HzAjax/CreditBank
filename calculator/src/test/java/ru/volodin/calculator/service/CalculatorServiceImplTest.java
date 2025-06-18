package ru.volodin.calculator.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.volodin.calculator.entity.dto.api.request.LoanStatementRequestDto;
import ru.volodin.calculator.entity.dto.api.request.ScoringDataDto;
import ru.volodin.calculator.entity.dto.api.response.CreditDto;
import ru.volodin.calculator.entity.dto.api.response.LoanOfferDto;
import ru.volodin.calculator.entity.dto.internal.SimpleScoringInfoDto;
import ru.volodin.calculator.service.credit.CreditCalculation;
import ru.volodin.calculator.service.scoring.ScoringProvider;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CalculatorServiceImplTest {

    @Mock
    private ScoringProvider scoringProvider;

    @Mock
    private CreditCalculation creditCalculation;

    @InjectMocks
    private CalculatorServiceImpl calculatorService;

    private LoanStatementRequestDto loanStatementRequestDto;
    private ScoringDataDto scoringDataDto;

    @BeforeEach
    void setUp() {
        loanStatementRequestDto = new LoanStatementRequestDto();
        loanStatementRequestDto.setAmount(BigDecimal.valueOf(100000));
        loanStatementRequestDto.setTerm(12);

        scoringDataDto = new ScoringDataDto();
    }

    @Test
    void testCalculateLoan_success() {

        SimpleScoringInfoDto info1 = new SimpleScoringInfoDto(true, true, BigDecimal.valueOf(10), BigDecimal.ZERO);
        SimpleScoringInfoDto info2 = new SimpleScoringInfoDto(false, false, BigDecimal.valueOf(15), BigDecimal.ZERO);

        LoanOfferDto offer1 = new LoanOfferDto();
        offer1.setRate(BigDecimal.valueOf(10));
        LoanOfferDto offer2 = new LoanOfferDto();
        offer2.setRate(BigDecimal.valueOf(15));

        when(scoringProvider.simpleScoring()).thenReturn(List.of(info1, info2));
        when(creditCalculation.generateLoanOffer(BigDecimal.valueOf(100000), 12, List.of(info1, info2)))
                .thenReturn(List.of(offer2, offer1));

        List<LoanOfferDto> result = calculatorService.calculateLoan(loanStatementRequestDto);

        assertEquals(2, result.size());
        assertEquals(BigDecimal.valueOf(10), result.getFirst().getRate());

        verify(scoringProvider).simpleScoring();
        verify(creditCalculation).generateLoanOffer(BigDecimal.valueOf(100000), 12, List.of(info1, info2));
    }

    @Test
    void testCalculateCredit_success() {

        BigDecimal expectedRate = BigDecimal.valueOf(21);
        BigDecimal expectedInsurance = BigDecimal.valueOf(100000);

        CreditDto expectedCredit = CreditDto.builder()
                .amount(BigDecimal.valueOf(100000))
                .term(21)
                .monthlyPayment(BigDecimal.valueOf(9000))
                .rate(expectedRate)
                .psk(BigDecimal.valueOf(115000))
                .isInsuranceEnabled(true)
                .isSalaryClient(false)
                .paymentSchedule(List.of())
                .build();

        when(scoringProvider.fullScoring(scoringDataDto)).thenReturn(new BigDecimal[] {
                expectedRate,
                expectedInsurance
        });

        when(creditCalculation.calculate(scoringDataDto, expectedRate, expectedInsurance))
                .thenReturn(expectedCredit);

        CreditDto result = calculatorService.calculateCredit(scoringDataDto);

        assertEquals(expectedCredit.getAmount(), result.getAmount());
        assertEquals(expectedCredit.getTerm(), result.getTerm());
        assertEquals(expectedCredit.getMonthlyPayment(), result.getMonthlyPayment());
        assertEquals(expectedCredit.getRate(), result.getRate());
        assertEquals(expectedCredit.getPsk(), result.getPsk());
        assertEquals(expectedCredit.getIsInsuranceEnabled(), result.getIsInsuranceEnabled());
        assertEquals(expectedCredit.getIsSalaryClient(), result.getIsSalaryClient());
        assertEquals(expectedCredit.getPaymentSchedule(), result.getPaymentSchedule());

        verify(scoringProvider).fullScoring(scoringDataDto);
        verify(creditCalculation).calculate(scoringDataDto, expectedRate, expectedInsurance);
    }

}
