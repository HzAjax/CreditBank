package ru.volodin.CreditBank.service.credit;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.volodin.CreditBank.entity.dto.*;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
public class CreditCalculationImpl implements CreditCalculation{

    private static final BigDecimal ONE_HUNDRED = BigDecimal.valueOf(100);
    private static final BigDecimal TWELVE = BigDecimal.valueOf(12);

    @Value("${service.calculator.round}")
    private Integer countDigitAfterPoint;

    @Override
    public List<LoanOfferDto> generateLoanOffer(BigDecimal amount, Integer term, List<SimpleScoringInfoDto> listInfo) {
        List<LoanOfferDto> loanOffers = new ArrayList<>();

        log.info("Calculate possible loans");
        log.debug("Amount={}, term={}. Possible rate and insurance cost={}", amount, term, listInfo);

        for (SimpleScoringInfoDto info : listInfo) {

            BigDecimal totalAmount = amount.add(info.getRateAndInsuredServiceDto().getInsuredService());
            BigDecimal newRate = info.getRateAndInsuredServiceDto().getNewRate();
            BigDecimal monthlyRate = getMonthlyRate(newRate);
            BigDecimal monthlyPayment = getMonthlyPayment(totalAmount, term, newRate).setScale(countDigitAfterPoint, RoundingMode.HALF_EVEN);
            List<PaymentScheduleElementDto> schedule = getSchedule(monthlyPayment, monthlyRate, totalAmount, term);
            BigDecimal psk = getPsk(schedule);

            boolean isSalaryClient = info.getOfferCombination().isSalaryClient();
            boolean isInsuranceEnabled = info.getOfferCombination().isInsurance();

            loanOffers.add(new LoanOfferDto(
                    UUID.randomUUID(),
                    amount,
                    psk,
                    term,
                    monthlyPayment,
                    newRate,
                    isInsuranceEnabled,
                    isSalaryClient
            ));
        }

        log.debug("Loans: {}", loanOffers);
        log.info("Calculate possible loans is over");

        return loanOffers;
    }

    @Override
    public CreditDto calculate(ScoringDataDto scoringDataDto, BigDecimal newRate, BigDecimal insuredService) {

        log.info("Calculate credit for INN={}, amount={}, term={}, rate={}, insurance cost={}",
                scoringDataDto.getEmployment().getEmployerINN(),
                scoringDataDto.getAmount(),
                scoringDataDto.getTerm(),
                newRate,
                insuredService);

        BigDecimal monthlyRate = getMonthlyRate(newRate);
        BigDecimal totalAmount = scoringDataDto.getAmount().add(insuredService);
        BigDecimal monthlyPayment = getMonthlyPayment(totalAmount, scoringDataDto.getTerm(), newRate);
        List<PaymentScheduleElementDto> schedule = getSchedule(monthlyPayment, monthlyRate, totalAmount, scoringDataDto.getTerm());        BigDecimal psk = getPsk(schedule);

        CreditDto creditDto = new CreditDto(
                scoringDataDto.getAmount(),
                scoringDataDto.getTerm(),
                round(monthlyPayment),
                newRate,
                psk,
                scoringDataDto.getIsInsuranceEnabled(),
                scoringDataDto.getIsSalaryClient(),
                schedule
        );

        log.debug("CreditDto: {}", creditDto);
        log.info("Credit has been calculated");

        return creditDto;
    }

    private BigDecimal getPsk(List<PaymentScheduleElementDto> schedule) {
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

    private List<PaymentScheduleElementDto> getSchedule(BigDecimal monthlyPayment, BigDecimal monthlyRate, BigDecimal totalAmount, Integer term) {
        List<PaymentScheduleElementDto> schedule = new ArrayList<>();
        BigDecimal remainingDebt = totalAmount;

        for (int i = 1; i <= term; i++) {
            LocalDate month = LocalDate.now().plusMonths(i);
            BigDecimal interestPayment = remainingDebt.multiply(monthlyRate);
            BigDecimal debtPayment = monthlyPayment.subtract(interestPayment);
            remainingDebt = remainingDebt.subtract(debtPayment);

            PaymentScheduleElementDto dto = createScheduleElement(
                    i, month, monthlyPayment, interestPayment, debtPayment, remainingDebt
            );
            schedule.add(dto);
        }
        return schedule;
    }

    private BigDecimal getMonthlyPayment(BigDecimal totalAmount, Integer term, BigDecimal newRate) {
        BigDecimal monthlyPercent = getMonthlyRate(newRate);
        return totalAmount.multiply(
                monthlyPercent.add(
                        monthlyPercent.divide(
                                BigDecimal.ONE.add(monthlyPercent).pow(term).subtract(BigDecimal.ONE),
                                new MathContext(MathContext.DECIMAL128.getPrecision(), RoundingMode.HALF_EVEN)
                        )
                )
        );
    }

    private BigDecimal getMonthlyRate(BigDecimal newRate) {
        return newRate.divide(ONE_HUNDRED)
                .divide(TWELVE, new MathContext(MathContext.DECIMAL128.getPrecision(), RoundingMode.HALF_EVEN));
    }

    private BigDecimal round(BigDecimal value) {
        return value.setScale(countDigitAfterPoint, RoundingMode.HALF_EVEN);
    }

    private PaymentScheduleElementDto createScheduleElement(
            int number,
            LocalDate date,
            BigDecimal monthlyPayment,
            BigDecimal interestPayment,
            BigDecimal debtPayment,
            BigDecimal remainingDebt
    ) {
        return new PaymentScheduleElementDto(
                number,
                date,
                round(monthlyPayment),
                round(interestPayment),
                round(debtPayment),
                round(remainingDebt)
        );
    }
}
