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
import ru.volodin.deal.entity.dto.api.CreditDto;
import ru.volodin.deal.entity.dto.api.ScoringDataDto;
import ru.volodin.deal.exceptions.ScoringException;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreditService {

    private final CalculatorHttpClient calculatorClient;

    @Retryable(
            retryFor = { HttpServerErrorException.class, ResourceAccessException.class },
            maxAttemptsExpression = "${retry.offers.attempts}",
            backoff = @Backoff(delayExpression = "${retry.offers.delay}")
    )
    public CreditDto getCreditWithRetry(ScoringDataDto dto) {
        return calculatorClient.getCredit(dto);
    }

    @Recover
    public CreditDto recover(Exception e, ScoringDataDto dto) {
        throw new ScoringException("Scoring failed after retries: " + e.getMessage());
    }
}
