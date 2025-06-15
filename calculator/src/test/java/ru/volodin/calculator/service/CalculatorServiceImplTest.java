package ru.volodin.calculator.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.volodin.calculator.entity.dto.api.*;
import ru.volodin.calculator.entity.dto.enums.EmploymentStatus;
import ru.volodin.calculator.entity.dto.enums.Gender;
import ru.volodin.calculator.entity.dto.enums.MaritalStatus;
import ru.volodin.calculator.entity.dto.enums.Position;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class CalculatorServiceImplTest {

    @Autowired
    private CalculatorService calculatorService;

    private ScoringDataDto buildScoringDataDto() {
        return ScoringDataDto.builder()
                .amount(BigDecimal.valueOf(300000))
                .term(12)
                .birthdate(LocalDate.of(1990, 1, 1))
                .firstName("Ivan")
                .lastName("Petrov")
                .gender(Gender.MALE)
                .maritalStatus(MaritalStatus.MARRIED)
                .isInsuranceEnabled(true)
                .isSalaryClient(true)
                .employment(
                        EmploymentDto.builder()
                                .salary(BigDecimal.valueOf(30000))
                                .employmentStatus(EmploymentStatus.SELF_EMPLOYED)
                                .position(Position.TOP_MANAGER)
                                .workExperienceCurrent(5)
                                .workExperienceTotal(20)
                                .build()
                )
                .build();
    }

    private LoanStatementRequestDto buildLoanRequest() {
        return LoanStatementRequestDto.builder()
                .amount(BigDecimal.valueOf(300000))
                .term(12)
                .build();
    }

    @Test
    void calculateCredit_shouldReturnValidCreditDto() {
        ScoringDataDto dto = buildScoringDataDto();

        CreditDto credit = calculatorService.calculateCredit(dto);

        assertThat(credit).isNotNull();
        assertThat(credit.getAmount()).isEqualByComparingTo("300000");
        assertThat(credit.getRate()).isEqualByComparingTo("9"); // see explanation below
        assertThat(credit.getMonthlyPayment()).isGreaterThan(BigDecimal.ZERO);
        assertThat(credit.getPsk()).isGreaterThan(credit.getAmount());
        assertThat(credit.getPaymentSchedule()).hasSize(12);
    }

    @Test
    void calculateLoan_shouldReturnSortedLoanOffers() {
        LoanStatementRequestDto request = buildLoanRequest();

        List<LoanOfferDto> offers = calculatorService.calculateLoan(request);

        assertThat(offers).isNotEmpty();
        assertThat(offers).isSortedAccordingTo(Comparator.comparing(LoanOfferDto::getRate));

        LoanOfferDto first = offers.get(0);
        assertThat(first.getRate()).isLessThanOrEqualTo(offers.get(offers.size() - 1).getRate());
    }
}
