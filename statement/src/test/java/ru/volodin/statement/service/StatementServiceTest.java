package ru.volodin.statement.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.volodin.statement.client.deal.service.OffersService;
import ru.volodin.statement.client.deal.service.SelectOfferService;
import ru.volodin.statement.entity.dto.LoanOfferDto;
import ru.volodin.statement.entity.dto.LoanStatementRequestDto;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StatementServiceTest {

    @Mock
    private OffersService retryableOffersService;

    @Mock
    private SelectOfferService retryableStatementService;

    @InjectMocks
    private StatementService statementService;

    @Test
    void getLoanOffers_shouldThrowException_whenArgumentIsNull() {
        assertThatThrownBy(() -> statementService.getLoanOffers(null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void selectLoanOffer_shouldThrowException_whenArgumentIsNull() {
        assertThatThrownBy(() -> statementService.selectLoanOffer(null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void getLoanOffers_shouldCallOffersServiceAndReturnList() {
        LoanStatementRequestDto request = new LoanStatementRequestDto();
        LoanOfferDto offer1 = new LoanOfferDto();
        LoanOfferDto offer2 = new LoanOfferDto();
        List<LoanOfferDto> expected = List.of(offer1, offer2);

        when(retryableOffersService.getLoanOffersWithRetry(request)).thenReturn(expected);

        List<LoanOfferDto> result = statementService.getLoanOffers(request);

        assertThat(result).isEqualTo(expected);
        verify(retryableOffersService, times(1)).getLoanOffersWithRetry(request);
        verifyNoInteractions(retryableStatementService);
    }

    @Test
    void selectLoanOffer_shouldCallSelectOfferService() {
        LoanOfferDto offer = new LoanOfferDto();

        statementService.selectLoanOffer(offer);

        verify(retryableStatementService, times(1)).setOfferWithRetry(offer);
        verifyNoInteractions(retryableOffersService);
    }
}
