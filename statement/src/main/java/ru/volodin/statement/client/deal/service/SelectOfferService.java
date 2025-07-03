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
        dealClient.setOffer(loanOfferDto);
    }

    @Recover
    public void recover(Exception e, LoanOfferDto loanOfferDto) {
        throw new RuntimeException("Select Loan offer retry failed: " + e.getMessage(), e);
    }
}
