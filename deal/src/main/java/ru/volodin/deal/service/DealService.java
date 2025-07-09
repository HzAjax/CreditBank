package ru.volodin.deal.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.mylib.exception.ScoringException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
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
import ru.volodin.deal.mappers.ClientMapper;
import ru.volodin.deal.mappers.CreditMapper;
import ru.volodin.deal.mappers.ScoringMapper;
import ru.volodin.deal.repository.ClientRepository;
import ru.volodin.deal.repository.StatementRepository;
import ru.volodin.deal.client.calculator.service.CreditService;
import ru.volodin.deal.client.calculator.service.OfferService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class DealService {

    private final ClientRepository clientRepository;
    private final StatementRepository statementRepository;

    private final ClientMapper clientMapper;
    private final ScoringMapper scoringMapper;
    private final CreditMapper creditMapper;

    private final CreditService retryableCreditService;
    private final OfferService retryableOfferService;

    public List<LoanOfferDto> calculateLoanOffers(LoanStatementRequestDto loanStatement) {

        if (loanStatement == null) {
            throw new IllegalArgumentException("LoanStatementRequestDto must not be null");
        }

        log.info("Received request to get loan offers for client.");

        ClientEntity mapperClient = clientMapper.toClient(loanStatement);
        mapperClient.getPassport().setPassportUUID(UUID.randomUUID());
        ClientEntity client = clientRepository.save(mapperClient);

        StatementEntity statementToSave = new StatementEntity();
        statementToSave.setCreationDate(LocalDateTime.now());
        statementToSave.setStatus(ApplicationStatus.PREAPPROVAL);
        statementToSave.setClient(client);
        statementToSave.getStatusHistory().add(StatusHistory.builder()
                .status(ApplicationStatus.PREAPPROVAL.name())
                .type(ChangeType.AUTOMATIC)
                .time(LocalDateTime.now())
                .build());
        StatementEntity statement = statementRepository.save(statementToSave);
        log.debug("Statement with ID {} has been created.", statement.getStatementId());

        List<LoanOfferDto> offersFromService = retryableOfferService.getLoanOffersWithRetry(loanStatement);
                List<LoanOfferDto> offers = offersFromService.stream()
                .map(oldOffer -> new LoanOfferDto(statement.getStatementId(), oldOffer))
                .toList();

        log.info("Generated {} loan offers for client {}.", offers.size(), client.getClientId());

        return offers;
    }

    public void selectLoanOffer(LoanOfferDto loanOffer) {

        if (loanOffer == null || loanOffer.getStatementId() == null) {
            throw new IllegalArgumentException("Loan offer or statement ID must not be null");
        }

        UUID statementId = loanOffer.getStatementId();
        StatementEntity statement = getStatementById(statementId);
        statement.setAppliedOffer(loanOffer);
        updateStatus(statementId, ApplicationStatus.APPROVED, ChangeType.AUTOMATIC);
        statementRepository.save(statement);

        log.debug("Loan offer selected and statement ID {} updated to status APPROVED.", statementId);
    }

    @Transactional(noRollbackFor = ScoringException.class)
    public void calculateCredit(UUID statementId, FinishRegistrationRequestDto finishRegistration) {
        StatementEntity statement = getStatementById(statementId);

        if (statement.getAppliedOffer() == null) {
            log.error("No loan offer selected for statement ID {}.", statementId);
            throw new IllegalArgumentException("Please select a loan offer to proceed with your application.");
        }

        ScoringDataDto scoringDataDto = scoringMapper.toScoringDataDto(statement, finishRegistration);
        CreditDto creditDto;

        try {
            creditDto = retryableCreditService.getCreditWithRetry(scoringDataDto);
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

        log.debug("Updating credit for statement ID {}.", statement.getStatementId());

        CreditEntity credit = creditMapper.toCredit(creditDto);
        credit.setStatus(CreditStatus.CALCULATED);

        if (statement.getCredit() == null) {
            statement.setCredit(credit);
            statementRepository.save(statement);
        }

        ClientEntity client = statement.getClient();

        log.debug("Updating client with ID {} using finish registration data.", client.getClientId());

        clientMapper.updateClientFromScoringData(client, scoringDataDto);
        client.getEmployment().setEmploymentUUID(UUID.randomUUID());
        clientRepository.save(client);
    }

    public StatementEntity getStatementById(UUID statementId) {
        return statementRepository.findById(statementId)
                .orElseThrow(() -> new EntityNotFoundException("StatementId " + statementId + " not found"));
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
