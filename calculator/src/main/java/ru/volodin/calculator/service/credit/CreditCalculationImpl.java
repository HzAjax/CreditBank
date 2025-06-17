package ru.volodin.calculator.service.credit;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.volodin.calculator.entity.dto.api.response.CreditDto;
import ru.volodin.calculator.entity.dto.api.response.LoanOfferDto;
import ru.volodin.calculator.entity.dto.api.response.PaymentScheduleElementDto;
import ru.volodin.calculator.entity.dto.api.request.ScoringDataDto;
import ru.volodin.calculator.entity.dto.internal.SimpleScoringInfoDto;
import ru.volodin.calculator.service.credit.helper.CalculatorHelper;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreditCalculationImpl implements CreditCalculation {

    private final CalculatorHelper calculatorHelper;

    @Value("${service.calculator.round}")
    private Integer countDigitAfterPoint;

    @Override
    public List<LoanOfferDto> generateLoanOffer(BigDecimal amount, Integer term, List<SimpleScoringInfoDto> listInfo) {

        List<LoanOfferDto> loanOffers = new ArrayList<>();

        log.info("Calculate possible loans");
        log.debug("Amount={}, term={}. Possible rate and insurance cost={}", amount, term, listInfo);

        for (SimpleScoringInfoDto info : listInfo) {

            BigDecimal totalAmount = amount.add(info.getInsurance());
            BigDecimal newRate = info.getNewRate();
            BigDecimal monthlyRate = calculatorHelper.getMonthlyRate(newRate);
            BigDecimal monthlyPayment = calculatorHelper.getMonthlyPayment(totalAmount, term, newRate)
                    .setScale(countDigitAfterPoint, RoundingMode.HALF_EVEN);
            List<PaymentScheduleElementDto> schedule = calculatorHelper.getSchedule(monthlyPayment, monthlyRate, totalAmount, term);
            BigDecimal psk = calculatorHelper.getPsk(schedule);

            boolean isSalaryClient = info.isSalaryClient();
            boolean isInsuranceEnabled = info.isInsurance();

            loanOffers.add(LoanOfferDto.builder()
                    .statementId(UUID.randomUUID())
                    .requestedAmount(amount)
                    .totalAmount(psk)
                    .term(term)
                    .monthlyPayment(monthlyPayment)
                    .rate(newRate)
                    .isInsuranceEnabled(isInsuranceEnabled)
                    .isSalaryClient(isSalaryClient)
                    .build());
        }

        log.debug("Loans: {}", loanOffers);
        log.info("Calculate possible loans is over");

        return loanOffers;
    }

    @Override
    public CreditDto calculate(ScoringDataDto scoringDataDto, BigDecimal newRate, BigDecimal insuredService) {

        log.info("Calculate credit for FIO={} {} , amount={}, term={}, rate={}, insurance cost={}",
                scoringDataDto.getFirstName(),
                scoringDataDto.getLastName(),
                scoringDataDto.getAmount(),
                scoringDataDto.getTerm(),
                newRate,
                insuredService);

        BigDecimal monthlyRate = calculatorHelper.getMonthlyRate(newRate);
        BigDecimal totalAmount = scoringDataDto.getAmount().add(insuredService);
        BigDecimal monthlyPayment = calculatorHelper.getMonthlyPayment(totalAmount, scoringDataDto.getTerm(), newRate);
        List<PaymentScheduleElementDto> schedule = calculatorHelper.getSchedule(monthlyPayment, monthlyRate, totalAmount, scoringDataDto.getTerm());
        BigDecimal psk = calculatorHelper.getPsk(schedule);

        CreditDto creditDto = new CreditDto(
                scoringDataDto.getAmount(),
                scoringDataDto.getTerm(),
                calculatorHelper.round(monthlyPayment),
                newRate,
                calculatorHelper.round(psk),
                scoringDataDto.getIsInsuranceEnabled(),
                scoringDataDto.getIsSalaryClient(),
                schedule
        );

        log.debug("CreditDto: {}", creditDto);
        log.info("Credit has been calculated");

        return creditDto;
    }
}
