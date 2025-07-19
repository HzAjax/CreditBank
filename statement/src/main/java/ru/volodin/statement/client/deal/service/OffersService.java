package ru.volodin.statement.client.deal.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import ru.volodin.errorhandling.exception.OffersException;
import ru.volodin.statement.client.DealHttpClient;
import ru.volodin.statement.entity.dto.LoanOfferDto;
import ru.volodin.statement.entity.dto.LoanStatementRequestDto;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OffersService {

    private final DealHttpClient dealClient;

    @Retryable(
            retryFor = { HttpServerErrorException.class, ResourceAccessException.class },
            maxAttemptsExpression = "${retry.offers.attempts}",
            backoff = @Backoff(delayExpression = "${retry.offers.delay}")
    )
    public List<LoanOfferDto> getLoanOffersWithRetry(LoanStatementRequestDto request) {
        log.info("Calling Deal service to get loan offers...");
        log.debug("LoanStatementRequestDto payload: {}", request);
        try {
            List<LoanOfferDto> response = dealClient.getStatement(request);
            log.info("Successfully received loan offers from Deal service.");
            log.debug("Received offers: {}", response);
            return response;
        } catch (Exception e) {
            log.warn("Temporary failure when calling Deal service (retryable): {}", e.getMessage());
            throw e;
        }
    }

    @Recover
    public List<LoanOfferDto> recover(Exception e, LoanStatementRequestDto request) {
        log.error("Failed to get loan offers from Deal service after all retries. Error: {}", e.getMessage(), e);

        String rawBody = null;
        if (e instanceof HttpClientErrorException httpEx) {
            rawBody = httpEx.getResponseBodyAsString();
        }

        throw new OffersException("Loan offers retry failed", rawBody, e);
    }
}
