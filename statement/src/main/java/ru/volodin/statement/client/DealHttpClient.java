package ru.volodin.statement.client;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;
import ru.volodin.statement.entity.dto.LoanOfferDto;
import ru.volodin.statement.entity.dto.LoanStatementRequestDto;


import java.util.List;

@HttpExchange
public interface DealHttpClient {

    @PostExchange("${client.deal.path.statement}")
    List<LoanOfferDto> getStatement(@RequestBody LoanStatementRequestDto requestDto);

    @PostExchange("${client.deal.path.select}")
    void setOffer(@RequestBody LoanOfferDto loanOfferDto);
}
