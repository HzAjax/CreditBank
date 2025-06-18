package ru.volodin.deal.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import ru.volodin.deal.entity.Client;
import ru.volodin.deal.entity.Credit;
import ru.volodin.deal.entity.Statement;
import ru.volodin.deal.entity.dto.api.CreditDto;
import ru.volodin.deal.entity.dto.api.FinishRegistrationRequestDto;
import ru.volodin.deal.entity.dto.api.LoanOfferDto;
import ru.volodin.deal.entity.dto.api.LoanStatementRequestDto;
import ru.volodin.deal.entity.dto.api.ScoringDataDto;
import ru.volodin.deal.entity.dto.enums.ApplicationStatus;
import ru.volodin.deal.entity.dto.enums.ChangeType;
import ru.volodin.deal.entity.dto.enums.CreditStatus;
import ru.volodin.deal.exceptions.ScoringException;
import ru.volodin.deal.exceptions.StatementNotFoundException;
import ru.volodin.deal.mappers.ClientMapper;
import ru.volodin.deal.mappers.CreditMapper;
import ru.volodin.deal.mappers.ScoringMapper;
import ru.volodin.deal.repository.ClientRepository;
import ru.volodin.deal.repository.StatementRepository;
import ru.volodin.deal.restclient.CalculatorRestClient;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DealServiceImpl implements  DealService{

    private final ClientRepository clientRepository;
    private final StatementRepository statementRepository;

    private final ClientMapper clientMapper;
    private final ScoringMapper scoringMapper;
    private final CreditMapper creditMapper;

    private final CalculatorRestClient calculatorClient;

    @Override
    public List<LoanOfferDto> calculateLoanOffers(LoanStatementRequestDto loanStatement) {
        log.info("Received request to get loan offers for client.");

        Client client = saveClient(loanStatement);
        Statement statement = createStatement(client);
        List<LoanOfferDto> offers = generateLoanOffers(loanStatement, statement);

        log.info("Generated {} loan offers for client {}.", offers.size(), client.getClientId());

        return offers;
    }

    private Client saveClient (LoanStatementRequestDto loanStatement) {
        Client client = clientMapper.toClient(loanStatement);
        client.getPassport().setPassportUUID(UUID.randomUUID());
        Client savedClient = clientRepository.save(client);

        log.info("Client with ID {} has been saved.", savedClient.getClientId());

        return savedClient;
    }

    private Statement createStatement (Client client) {
        Statement statement = new Statement();
        statement.setCreationDate(LocalDateTime.now());
        statement.setStatus(ApplicationStatus.PREAPPROVAL, ChangeType.AUTOMATIC);
        statement.setClient(client);
        Statement savedStatement = statementRepository.save(statement);

        log.debug("Statement with ID {} has been created.", savedStatement.getStatementId());

        return savedStatement;
    }

    private List<LoanOfferDto> generateLoanOffers(LoanStatementRequestDto loanStatement, Statement statement) {
        List<LoanOfferDto> offers = calculatorClient.calculateLoanOffers(loanStatement);
        return offers.stream()
                .map(oldOffer -> new LoanOfferDto(statement.getStatementId(), oldOffer))
                .toList();
    }

    @Override
    public void selectLoanOffer(LoanOfferDto loanOffer) {
        UUID statementId = loanOffer.getStatementId();
        Statement statement = getStatementById(statementId);
        statement.setAppliedOffer(loanOffer);
        statement.setStatus(ApplicationStatus.APPROVED, ChangeType.AUTOMATIC);
        statementRepository.save(statement);

        log.debug("Loan offer selected and statement ID {} updated to status APPROVED.", statementId);
    }

    @Override
    public void calculateCredit(UUID statementId, FinishRegistrationRequestDto finishRegistration) {
        Statement statement = getStatementById(statementId);

        if (statement.getAppliedOffer() == null) {
            log.error("No loan offer selected for statement ID {}.", statementId);
            throw new IllegalArgumentException("First, select loan offer!");
        }

        ScoringDataDto scoringDataDto = scoringMapper.toScoringDataDto(statement, finishRegistration);
        CreditDto creditDto;

        try {
            creditDto = calculatorClient.getCredit(scoringDataDto);
            statement.setStatus(ApplicationStatus.CC_APPROVED, ChangeType.AUTOMATIC);
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.UNPROCESSABLE_ENTITY) {
                statement.setStatus(ApplicationStatus.CC_DENIED, ChangeType.AUTOMATIC);
                statementRepository.save(statement);

                throw new ScoringException(e.getResponseBodyAsString());
            }
            throw e;
        }
        log.debug("Mapping CreditDto={} to Credit entity for statement ID {}.", creditDto, statementId);

        updateCredit(statement, creditDto);
        updateClient(statement.getClient(), scoringDataDto);
    }

    public Statement getStatementById(UUID statementId) {
        return statementRepository.findById(statementId)
                .orElseThrow(() -> new StatementNotFoundException("StatementId " + statementId + " not found"));
    }

    private void updateCredit(Statement statement, CreditDto creditDto) {
        log.debug("Updating credit for statement ID {}.", statement.getStatementId());

        Credit credit = creditMapper.toCredit(creditDto);
        credit.setStatus(CreditStatus.CALCULATED);

        if (statement.getCredit() == null) {
            statement.setCredit(credit);
            statementRepository.save(statement);
        }
    }

    private void updateClient(Client client, ScoringDataDto scoringDataDto) {
        log.debug("Updating client with ID {} using finish registration data.", client.getClientId());

        clientMapper.updateClientFromScoringData(client, scoringDataDto);
        client.getEmployment().setEmploymentUUID(UUID.randomUUID());
        clientRepository.save(client);
    }
}
