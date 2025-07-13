package ru.volodin.deal.client.calculator.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import ru.volodin.deal.client.CalculatorHttpClient;
import ru.volodin.deal.entity.dto.api.CreditDto;
import ru.volodin.deal.entity.dto.api.ScoringDataDto;
import ru.volodin.errorhandling.exception.ScoringException;

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
        log.info("Calling Calculator service for credit calculation...");
        log.debug("ScoringDataDto payload: {}", dto);
        try {
            CreditDto credit = calculatorClient.getCredit(dto);
            log.info("Successfully received credit calculation from Calculator service.");
            log.debug("CreditDto response: {}", credit);
            return credit;
        } catch (Exception e) {
            log.warn("Temporary failure when calling Calculator service (retryable): {}", e.getMessage());
            throw e;
        }
    }

    @Recover
    public CreditDto recover(Exception e, ScoringDataDto dto) {
        log.error("Failed to get credit calculation from Calculator service after all retries. Error: {}", e.getMessage(), e);

        String rawBody = null;
        if (e instanceof HttpClientErrorException httpEx) {
            rawBody = httpEx.getResponseBodyAsString();
        }

        throw new ScoringException("Scoring failed after retries", rawBody, e);
    }
}
