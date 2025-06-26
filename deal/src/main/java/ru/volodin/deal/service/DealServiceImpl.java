package ru.volodin.deal.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import ru.volodin.deal.client.CalculatorHttpClient;
import ru.volodin.deal.entity.ClientEntity;
import ru.volodin.deal.entity.CreditEntity;
import ru.volodin.deal.entity.StatementEntity;
import ru.volodin.deal.entity.dto.api.CreditDto;
import ru.volodin.deal.entity.dto.api.FinishRegistrationRequestDto;
import ru.volodin.deal.entity.dto.api.LoanOfferDto;
import ru.volodin.deal.entity.dto.api.LoanStatementRequestDto;
import ru.volodin.deal.entity.dto.api.ScoringDataDto;
import ru.volodin.deal.entity.dto.enums.ApplicationStatus;
import ru.volodin.deal.entity.dto.enums.ChangeType;
import ru.volodin.deal.entity.dto.enums.CreditStatus;
import ru.volodin.deal.entity.jsonb.StatusHistory;
import ru.volodin.deal.exceptions.ScoringException;
import ru.volodin.deal.mappers.ClientMapper;
import ru.volodin.deal.mappers.CreditMapper;
import ru.volodin.deal.mappers.ScoringMapper;
import ru.volodin.deal.repository.ClientRepository;
import ru.volodin.deal.repository.StatementRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class DealServiceImpl {

    private final ClientRepository clientRepository;
    private final StatementRepository statementRepository;

    private final ClientMapper clientMapper;
    private final ScoringMapper scoringMapper;
    private final CreditMapper creditMapper;

    private final CalculatorHttpClient calculatorClient;

    public List<LoanOfferDto> calculateLoanOffers(LoanStatementRequestDto loanStatement) {

        if (loanStatement == null) {
            throw new IllegalArgumentException("LoanStatementRequestDto must not be null");
        }

        log.info("Received request to get loan offers for client.");

        ClientEntity client = saveClient(loanStatement);
        StatementEntity statement = createStatement(client);
        List<LoanOfferDto> offers = generateLoanOffers(loanStatement, statement);

        log.info("Generated {} loan offers for client {}.", offers.size(), client.getClientId());

        return offers;
    }

    private ClientEntity saveClient(LoanStatementRequestDto loanStatement) {
        ClientEntity client = clientMapper.toClient(loanStatement);
        client.getPassport().setPassportUUID(UUID.randomUUID());
        ClientEntity savedClient = clientRepository.save(client);

        log.info("Client with ID {} has been saved.", savedClient.getClientId());

        return savedClient;
    }

    private StatementEntity createStatement(ClientEntity client) {
        StatementEntity statement = new StatementEntity();
        statement.setCreationDate(LocalDateTime.now());
        statement.setStatus(ApplicationStatus.PREAPPROVAL);
        statement.setClient(client);

        statement.getStatusHistory().add(StatusHistory.builder()
                .status(ApplicationStatus.PREAPPROVAL.name())
                .type(ChangeType.AUTOMATIC)
                .time(LocalDateTime.now())
                .build());

        StatementEntity savedStatement = statementRepository.save(statement);

        log.debug("Statement with ID {} has been created.", savedStatement.getStatementId());
        return savedStatement;
    }

    private List<LoanOfferDto> generateLoanOffers(LoanStatementRequestDto loanStatement, StatementEntity statement) {
        List<LoanOfferDto> offers = calculatorClient.calculateLoanOffers(loanStatement);
        return offers.stream()
                .map(oldOffer -> new LoanOfferDto(statement.getStatementId(), oldOffer))
                .toList();
    }

    public void selectLoanOffer(LoanOfferDto loanOffer) {
        UUID statementId = loanOffer.getStatementId();
        StatementEntity statement = getStatementById(statementId);
        statement.setAppliedOffer(loanOffer);
        updateStatus(statementId, ApplicationStatus.APPROVED, ChangeType.AUTOMATIC);
        statementRepository.save(statement);

        log.debug("Loan offer selected and statement ID {} updated to status APPROVED.", statementId);
    }

    public void calculateCredit(UUID statementId, FinishRegistrationRequestDto finishRegistration) {
        StatementEntity statement = getStatementById(statementId);

        if (statement.getAppliedOffer() == null) {
            log.error("No loan offer selected for statement ID {}.", statementId);
            throw new IllegalArgumentException("Please select a loan offer to proceed with your application.");
        }

        ScoringDataDto scoringDataDto = scoringMapper.toScoringDataDto(statement, finishRegistration);
        CreditDto creditDto;

        try {
            creditDto = calculatorClient.getCredit(scoringDataDto);
            updateStatus(statementId, ApplicationStatus.CC_APPROVED, ChangeType.AUTOMATIC);
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.UNPROCESSABLE_ENTITY) {
                updateStatus(statementId, ApplicationStatus.CC_DENIED, ChangeType.AUTOMATIC);
                statementRepository.save(statement);

                throw new ScoringException(e.getResponseBodyAsString());
            }
            throw e;
        }
        log.debug("Mapping CreditDto={} to Credit entity for statement ID {}.", creditDto, statementId);

        updateCredit(statement, creditDto);
        updateClient(statement.getClient(), scoringDataDto);
    }

    public StatementEntity getStatementById(UUID statementId) {
        return statementRepository.findById(statementId)
                .orElseThrow(() -> new EntityNotFoundException("StatementId " + statementId + " not found"));
    }

    private void updateCredit(StatementEntity statement, CreditDto creditDto) {
        log.debug("Updating credit for statement ID {}.", statement.getStatementId());

        CreditEntity credit = creditMapper.toCredit(creditDto);
        credit.setStatus(CreditStatus.CALCULATED);

        if (statement.getCredit() == null) {
            statement.setCredit(credit);
            statementRepository.save(statement);
        }
    }

    private void updateClient(ClientEntity client, ScoringDataDto scoringDataDto) {
        log.debug("Updating client with ID {} using finish registration data.", client.getClientId());

        clientMapper.updateClientFromScoringData(client, scoringDataDto);
        client.getEmployment().setEmploymentUUID(UUID.randomUUID());
        clientRepository.save(client);
    }

    private void updateStatus(UUID statementId, ApplicationStatus status, ChangeType type) {
        StatementEntity statement = statementRepository.findById(statementId)
                .orElseThrow(() -> new EntityNotFoundException("Statement not found"));

        statement.setStatus(status);
        statement.getStatusHistory().add(
                StatusHistory.builder()
                        .status(status.name())
                        .type(type)
                        .time(LocalDateTime.now())
                        .build()
        );

        statementRepository.save(statement);
    }
}
