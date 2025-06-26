package ru.volodin.deal.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import ru.volodin.deal.entity.dto.api.LoanOfferDto;
import ru.volodin.deal.entity.dto.enums.ApplicationStatus;
import ru.volodin.deal.entity.jsonb.StatusHistory;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "statement")
@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class StatementEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "statement_id")
    private UUID statementId;

    @OneToOne
    @JoinColumn(name = "client_id", referencedColumnName = "client_id")
    private ClientEntity client;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "credit_id", referencedColumnName = "credit_id")
    private CreditEntity credit;

    @Enumerated(EnumType.STRING)
    @Column(name = "application_status")
    private ApplicationStatus status;

    @Column(name = "creation_date", columnDefinition = "timestamp")
    private LocalDateTime creationDate;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "applied_offer")
    private LoanOfferDto appliedOffer;

    @Column(name = "sign_date", columnDefinition = "timestamp")
    private LocalDateTime signDate;

    @Column(name = "ses_code")
    private String code;

    @Builder.Default
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "status_history")
    private List<StatusHistory> statusHistory = new ArrayList<>();

    public void addStatusHistory(StatusHistory status) {
        statusHistory.add(status);
    }
}
