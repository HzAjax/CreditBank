package ru.volodin.statement.client.deal.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import ru.volodin.statement.client.DealHttpClient;
import ru.volodin.statement.entity.dto.LoanOfferDto;

@Slf4j
@Service
@RequiredArgsConstructor
public class SelectOfferService {

    private final DealHttpClient dealClient;

    @Retryable(
            retryFor = { HttpServerErrorException.class, ResourceAccessException.class },
            maxAttemptsExpression = "${retry.offers.attempts}",
            backoff = @Backoff(delayExpression = "${retry.offers.delay}")
    )
    public void setOfferWithRetry(LoanOfferDto loanOfferDto) {
        log.info("Calling Deal service: sending LoanOfferDto...");
        log.debug("LoanOfferDto payload: {}", loanOfferDto);
        try {
            dealClient.setOffer(loanOfferDto);
            log.info("Successfully sent offer to Deal service.");
        } catch (Exception e) {
            log.warn("Temporary failure when calling Deal service (retryable): {}", e.getMessage());
            throw e;
        }
    }

    @Recover
    public void recover(Exception e, LoanOfferDto loanOfferDto) {
        log.error("Failed to send offer to Deal service after all retries. Error: {}", e.getMessage(), e);
        throw new RuntimeException("Select Loan offer retry failed: " + e.getMessage(), e);
    }
}
