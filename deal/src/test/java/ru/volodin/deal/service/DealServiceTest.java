package ru.volodin.deal.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import ru.volodin.deal.client.calculator.service.CreditService;
import ru.volodin.deal.client.calculator.service.OfferService;
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
import ru.volodin.deal.entity.jsonb.StatusHistory;
import ru.volodin.deal.exceptions.ScoringException;
import ru.volodin.deal.mappers.ClientMapper;
import ru.volodin.deal.mappers.CreditMapper;
import ru.volodin.deal.mappers.ScoringMapper;
import ru.volodin.deal.repository.ClientRepository;
import ru.volodin.deal.repository.StatementRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DealServiceTest {

    @Mock
    private ClientRepository clientRepository;
    @Mock
    private StatementRepository statementRepository;
    @Mock
    private ClientMapper clientMapper;
    @Mock
    private OfferService retryableOfferService;
    @Mock
    private ScoringMapper scoringMapper;
    @Mock
    private CreditMapper creditMapper;
    @Mock
    private CreditService retryableCreditService;

    @InjectMocks
    private DealService dealService;

    private LoanStatementRequestDto requestDto;
    private ClientEntity clientEntity;
    private StatementEntity savedStatement;
    private UUID statementId;
    private LoanOfferDto loanOffer;
    private StatementEntity statement;

    @BeforeEach
    void setUp() {
        statementId = UUID.randomUUID();

        requestDto = LoanStatementRequestDto.builder()
                .amount(BigDecimal.valueOf(300_000))
                .term(36)
                .firstName("Ivan")
                .lastName("Ivanov")
                .middleName("Ivanovich")
                .email("ivan@example.com")
                .birthdate(LocalDate.of(1990, 1, 1))
                .passportSeries("1234")
                .passportNumber("567890")
                .build();

        clientEntity = ClientEntity.builder()
                .firstName("Ivan")
                .lastName("Ivanov")
                .middleName("Ivanovich")
                .birthdate(LocalDate.of(1990, 1, 1))
                .email("ivan@example.com")
                .passport(new Passport())
                .build();

        savedStatement = new StatementEntity();
        savedStatement.setStatementId(statementId);
        savedStatement.setClient(clientEntity);
        savedStatement.setStatus(ApplicationStatus.PREAPPROVAL);
        savedStatement.setStatusHistory(new java.util.ArrayList<>(List.of(
                StatusHistory.builder()
                        .status(ApplicationStatus.PREAPPROVAL.name())
                        .time(LocalDate.now().atStartOfDay())
                        .build()
        )));

        loanOffer = LoanOfferDto.builder()
                .statementId(statementId)
                .build();

        statement = new StatementEntity();
        statement.setStatementId(statementId);
    }

    @Test
    void testCalculateLoanOffers_success() {
        when(clientMapper.toClient(requestDto)).thenReturn(clientEntity);
        when(clientRepository.save(any())).thenReturn(clientEntity);
        when(statementRepository.save(any())).thenReturn(savedStatement);

        LoanOfferDto offerFromService = LoanOfferDto.builder()
                .requestedAmount(BigDecimal.valueOf(300_000))
                .totalAmount(BigDecimal.valueOf(506_261.16))
                .term(36)
                .monthlyPayment(BigDecimal.valueOf(14_062.81))
                .rate(BigDecimal.valueOf(16))
                .isInsuranceEnabled(true)
                .isSalaryClient(true)
                .build();

        when(retryableOfferService.getLoanOffersWithRetry(requestDto))
                .thenReturn(List.of(offerFromService));

        List<LoanOfferDto> result = dealService.calculateLoanOffers(requestDto);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getStatementId()).isEqualTo(statementId);

        verify(clientRepository).save(any());
        verify(statementRepository, times(1)).save(any());
        verify(retryableOfferService).getLoanOffersWithRetry(requestDto);
    }

    @Test
    void testCalculateLoanOffers_nullInput_throwsException() {
        assertThrows(IllegalArgumentException.class,
                () -> dealService.calculateLoanOffers(null));
    }

    @Test
    void testSelectLoanOffer_success() {
        when(statementRepository.findById(statementId)).thenReturn(Optional.of(statement));

        dealService.selectLoanOffer(loanOffer);

        assertEquals(loanOffer, statement.getAppliedOffer());
        assertEquals(ApplicationStatus.APPROVED, statement.getStatus());

        verify(statementRepository, times(2)).findById(statementId); // getStatementById + updateStatus
        verify(statementRepository, times(2)).save(statement);
    }

    @Test
    void testSelectLoanOffer_nullDto_throws() {
        assertThrows(IllegalArgumentException.class, () -> dealService.selectLoanOffer(null));
    }

    @Test
    void testSelectLoanOffer_nullStatementId_throws() {
        loanOffer.setStatementId(null);
        assertThrows(IllegalArgumentException.class, () -> dealService.selectLoanOffer(loanOffer));
    }

    @Test
    void testSelectLoanOffer_statementNotFound_throws() {
        when(statementRepository.findById(statementId)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> dealService.selectLoanOffer(loanOffer));
    }

    @Test
    void testCalculateCredit_success() {
        // given
        FinishRegistrationRequestDto finishRegistration = new FinishRegistrationRequestDto();
        StatementEntity st = new StatementEntity();
        ClientEntity client = new ClientEntity();
        client.setEmployment(new Employment());
        st.setAppliedOffer(new LoanOfferDto());
        st.setClient(client);

        ScoringDataDto scoringData = new ScoringDataDto();
        CreditDto creditDto = new CreditDto();
        CreditEntity credit = new CreditEntity();

        when(statementRepository.findById(statementId)).thenReturn(Optional.of(st));
        when(scoringMapper.toScoringDataDto(st, finishRegistration)).thenReturn(scoringData);
        when(retryableCreditService.getCreditWithRetry(scoringData)).thenReturn(creditDto);
        when(creditMapper.toCredit(creditDto)).thenReturn(credit);

        // when
        dealService.calculateCredit(statementId, finishRegistration);

        // then
        verify(statementRepository, atLeastOnce()).save(st);
        verify(clientRepository).save(client);
        verify(clientMapper).updateClientFromScoringData(client, scoringData);
    }

    @Test
    void testCalculateCredit_noAppliedOffer_throwsException() {
        StatementEntity st = new StatementEntity(); // no applied offer
        when(statementRepository.findById(statementId)).thenReturn(Optional.of(st));

        assertThrows(IllegalArgumentException.class,
                () -> dealService.calculateCredit(statementId, new FinishRegistrationRequestDto()));
    }

    @Test
    void testCalculateCredit_scoring422_throwsScoringException() {
        StatementEntity st = new StatementEntity();
        st.setAppliedOffer(new LoanOfferDto());

        HttpClientErrorException http422 = HttpClientErrorException.create(
                HttpStatus.UNPROCESSABLE_ENTITY, "422", null, "error".getBytes(), null);

        when(statementRepository.findById(statementId)).thenReturn(Optional.of(st));
        when(scoringMapper.toScoringDataDto(any(), any())).thenReturn(new ScoringDataDto());
        when(retryableCreditService.getCreditWithRetry(any())).thenThrow(http422);

        assertThrows(ScoringException.class, () ->
                dealService.calculateCredit(statementId, new FinishRegistrationRequestDto()));

        verify(statementRepository, times(2)).save(st);
    }

    @Test
    void testCalculateCredit_otherHttpClientError_throwsOriginal() {
        StatementEntity st = new StatementEntity();
        st.setAppliedOffer(new LoanOfferDto());

        HttpClientErrorException http400 = HttpClientErrorException.create(
                HttpStatus.BAD_REQUEST, "400", null, "bad".getBytes(), null);

        when(statementRepository.findById(statementId)).thenReturn(Optional.of(st));
        when(scoringMapper.toScoringDataDto(any(), any())).thenReturn(new ScoringDataDto());
        when(retryableCreditService.getCreditWithRetry(any())).thenThrow(http400);

        assertThrows(HttpClientErrorException.class, () ->
                dealService.calculateCredit(statementId, new FinishRegistrationRequestDto()));
    }
}
