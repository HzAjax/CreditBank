package ru.volodin.deal.client.calculator.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import ru.volodin.deal.client.CalculatorHttpClient;
import ru.volodin.deal.entity.dto.api.LoanOfferDto;
import ru.volodin.deal.entity.dto.api.LoanStatementRequestDto;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OfferService {

    private final CalculatorHttpClient calculatorClient;

    @Retryable(
            retryFor = { HttpServerErrorException.class, ResourceAccessException.class },
            maxAttemptsExpression = "${retry.offers.attempts}",
            backoff = @Backoff(delayExpression = "${retry.offers.delay}")
    )
    public List<LoanOfferDto> getLoanOffersWithRetry(LoanStatementRequestDto request) {
        return calculatorClient.calculateLoanOffers(request);
    }

    @Recover
    public List<LoanOfferDto> recover(Exception e, LoanStatementRequestDto request) {
        throw new RuntimeException("Loan offers retry failed: " + e.getMessage(), e);
    }
}
