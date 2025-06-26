package ru.volodin.deal.service.retry;

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

@Service
public class RetryableOfferService {

    private final CalculatorHttpClient calculatorClient;

    public RetryableOfferService(CalculatorHttpClient calculatorClient) {
        this.calculatorClient = calculatorClient;
    }

    @Retryable(
            retryFor = { HttpServerErrorException.class, ResourceAccessException.class },
            maxAttemptsExpression = "#{@offerRetryProperties.attempts}",
            backoff = @Backoff(delayExpression = "#{@offerRetryProperties.delay}")
    )
    public List<LoanOfferDto> getLoanOffersWithRetry(LoanStatementRequestDto request) {
        return calculatorClient.calculateLoanOffers(request);
    }

    @Recover
    public List<LoanOfferDto> recover(Exception e, LoanStatementRequestDto request) {
        throw new RuntimeException("Loan offers retry failed: " + e.getMessage(), e);
    }
}
