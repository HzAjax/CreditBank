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
        return dealClient.getStatement(request);
    }

    @Recover
    public List<LoanOfferDto> recover(Exception e, LoanStatementRequestDto request) {
        throw new RuntimeException("Loan offers retry failed: " + e.getMessage(), e);
    }
}
