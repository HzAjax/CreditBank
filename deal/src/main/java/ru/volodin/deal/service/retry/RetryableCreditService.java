package ru.volodin.deal.service.retry;

import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import ru.volodin.deal.client.CalculatorHttpClient;
import ru.volodin.deal.entity.dto.api.CreditDto;
import ru.volodin.deal.entity.dto.api.ScoringDataDto;
import ru.volodin.deal.exceptions.ScoringException;

@Service
public class RetryableCreditService {

    private final CalculatorHttpClient calculatorClient;

    public RetryableCreditService(CalculatorHttpClient calculatorClient) {
        this.calculatorClient = calculatorClient;
    }

    @Retryable(
            retryFor = { HttpServerErrorException.class, ResourceAccessException.class },
            maxAttemptsExpression = "#{@offerRetryProperties.attempts}",
            backoff = @Backoff(delayExpression = "#{@offerRetryProperties.delay}")
    )
    public CreditDto getCreditWithRetry(ScoringDataDto dto) {
        return calculatorClient.getCredit(dto);
    }

    @Recover
    public CreditDto recover(Exception e, ScoringDataDto dto) {
        throw new ScoringException("Scoring failed after retries: " + e.getMessage());
    }
}
