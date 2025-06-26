package ru.volodin.deal.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
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
import ru.volodin.deal.entity.jsonb.Employment;
import ru.volodin.deal.entity.jsonb.Passport;
import ru.volodin.deal.exceptions.ScoringException;
import ru.volodin.deal.repository.ClientRepository;
import ru.volodin.deal.repository.StatementRepository;
import ru.volodin.deal.mappers.ClientMapper;
import ru.volodin.deal.mappers.ScoringMapper;
import ru.volodin.deal.mappers.CreditMapper;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DealServiceTest {

    @Mock
    private ClientRepository clientRepository;
    @Mock
    private StatementRepository statementRepository;
    @Mock
    private ClientMapper clientMapper;
    @Mock
    private ScoringMapper scoringMapper;
    @Mock
    private CreditMapper creditMapper;
    @Mock
    private CalculatorHttpClient calculatorClient;

    @InjectMocks
    private DealServiceImpl dealService;

    @Test
    void calculateLoanOffersTest() {
        LoanStatementRequestDto loanStatement = new LoanStatementRequestDto();
        ClientEntity client = new ClientEntity();
        client.setPassport(new Passport());
        StatementEntity statement = new StatementEntity();
        statement.setStatementId(UUID.randomUUID());

        List<LoanOfferDto> remoteOffers = List.of(new LoanOfferDto(), new LoanOfferDto());

        when(clientMapper.toClient(loanStatement)).thenReturn(client);
        when(clientRepository.save(client)).thenReturn(client);
        when(statementRepository.save(any(StatementEntity.class))).thenReturn(statement);
        when(calculatorClient.calculateLoanOffers(loanStatement)).thenReturn(remoteOffers);

        List<LoanOfferDto> result = dealService.calculateLoanOffers(loanStatement);

        assertThat(result).hasSize(2);
        assertThat(result.getFirst().getStatementId()).isEqualTo(statement.getStatementId());
    }

    @Test
    void selectLoanOfferTest() {
        UUID statementId = UUID.randomUUID();
        LoanOfferDto loanOffer = mock(LoanOfferDto.class);
        when(loanOffer.getStatementId()).thenReturn(statementId);

        StatementEntity statement = mock(StatementEntity.class);

        when(statementRepository.findById(statementId)).thenReturn(java.util.Optional.of(statement));

        dealService.selectLoanOffer(loanOffer);

        verify(statement).setAppliedOffer(loanOffer);
        verify(statementRepository).save(statement);
    }

    @DisplayName("Call the client and update the objects on success")
    @Test
    void calculateCredit_callClient() {
        UUID statementId = UUID.randomUUID();
        FinishRegistrationRequestDto finishDto = new FinishRegistrationRequestDto();
        ScoringDataDto scoringDto = new ScoringDataDto();
        CreditDto creditDto = new CreditDto();
        CreditEntity credit = new CreditEntity();

        ClientEntity client = new ClientEntity();
        client.setEmployment(new Employment());
        StatementEntity statement = new StatementEntity();
        statement.setStatementId(statementId);
        statement.setClient(client);
        statement.setAppliedOffer(new LoanOfferDto());

        when(statementRepository.findById(statementId)).thenReturn(Optional.of(statement));
        when(scoringMapper.toScoringDataDto(statement, finishDto)).thenReturn(scoringDto);
        when(calculatorClient.getCredit(scoringDto)).thenReturn(creditDto);
        when(creditMapper.toCredit(creditDto)).thenReturn(credit);

        dealService.calculateCredit(statementId, finishDto);

        assertThat(statement.getStatus()).isEqualTo(ApplicationStatus.CC_APPROVED);
        verify(scoringMapper).toScoringDataDto(statement, finishDto);
        verify(calculatorClient).getCredit(scoringDto);
        verify(creditMapper).toCredit(creditDto);
        verify(clientMapper).updateClientFromScoringData(client, scoringDto);
        assertThat(client.getEmployment().getEmploymentUUID()).isNotNull();
        verify(clientRepository).save(client);
        verify(statementRepository).save(statement);
    }

    @DisplayName("Set the status to Denied and create an exception for scoring when the score returns the value 422")
    @Test
    void calculateCredit_setDeniedStatus() {
        UUID statementId = UUID.randomUUID();
        StatementEntity statement = new StatementEntity();
        statement.setStatementId(statementId);
        statement.setAppliedOffer(new LoanOfferDto());
        statement.setClient(new ClientEntity());

        FinishRegistrationRequestDto finishDto = new FinishRegistrationRequestDto();
        ScoringDataDto scoringDto = new ScoringDataDto();

        when(statementRepository.findById(statementId)).thenReturn(Optional.of(statement));
        when(scoringMapper.toScoringDataDto(statement, finishDto)).thenReturn(scoringDto);

        HttpClientErrorException httpException = mock(HttpClientErrorException.class);
        when(httpException.getStatusCode()).thenReturn(HttpStatus.UNPROCESSABLE_ENTITY);
        when(httpException.getResponseBodyAsString()).thenReturn("scoring error");

        when(calculatorClient.getCredit(scoringDto)).thenThrow(httpException);

        assertThatThrownBy(() -> dealService.calculateCredit(statementId, finishDto))
                .isInstanceOf(ScoringException.class)
                .hasMessage("scoring error");

        assertThat(statement.getStatus()).isEqualTo(ApplicationStatus.CC_DENIED);

        verify(statementRepository).save(statement);
    }

    @DisplayName("Should generate an exception if the offer is not selected.")
    @Test
    void calculateCredit_ThrowException() {
        UUID statementId = UUID.randomUUID();
        StatementEntity statement = new StatementEntity();
        statement.setStatementId(statementId);

        FinishRegistrationRequestDto finishDto = new FinishRegistrationRequestDto();

        when(statementRepository.findById(statementId)).thenReturn(Optional.of(statement));

        assertThatThrownBy(() -> dealService.calculateCredit(statementId, finishDto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("First, select loan offer!");
    }
}
