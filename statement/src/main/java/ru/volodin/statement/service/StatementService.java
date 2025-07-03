package ru.volodin.statement.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.volodin.statement.client.deal.service.OffersService;
import ru.volodin.statement.client.deal.service.SelectOfferService;
import ru.volodin.statement.entity.dto.LoanOfferDto;
import ru.volodin.statement.entity.dto.LoanStatementRequestDto;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StatementService {

    private final OffersService retryableOffersService;
    private final SelectOfferService retryableStatementService;

    public List<LoanOfferDto> getLoanOffers (LoanStatementRequestDto loanStatement) {
        return retryableOffersService.getLoanOffersWithRetry(loanStatement);
    }

    public void selectLoanOffer (LoanOfferDto loanOffer) {
        retryableStatementService.setOfferWithRetry(loanOffer);
    }
}
