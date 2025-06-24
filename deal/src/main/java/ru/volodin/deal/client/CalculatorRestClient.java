package ru.volodin.deal.client;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import ru.volodin.deal.entity.dto.api.CreditDto;
import ru.volodin.deal.entity.dto.api.LoanOfferDto;
import ru.volodin.deal.entity.dto.api.LoanStatementRequestDto;
import ru.volodin.deal.entity.dto.api.ScoringDataDto;

import java.util.List;

@Component
public class CalculatorRestClient {


    private final RestClient restClient;

    public CalculatorRestClient(RestClient calculatorRestClient) {
        this.restClient = calculatorRestClient;
    }

    public List<LoanOfferDto> calculateLoanOffers(LoanStatementRequestDto loanStatementRequestDto) {
        return restClient.post()
                .uri("/calculator/offers")
                .body(loanStatementRequestDto)
                .retrieve()
                .body(new ParameterizedTypeReference<List<LoanOfferDto>>() {
                });
    }

    public CreditDto getCredit(ScoringDataDto scoringDataDto) {
        return restClient.post()
                .uri("/calculator/calc")
                .body(scoringDataDto)
                .retrieve()
                .body(CreditDto.class);
    }
}
