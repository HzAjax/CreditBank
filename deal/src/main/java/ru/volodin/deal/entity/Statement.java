package ru.volodin.deal.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import ru.volodin.deal.entity.dto.api.LoanOfferDto;
import ru.volodin.deal.entity.dto.enums.ApplicationStatus;
import ru.volodin.deal.entity.dto.enums.ChangeType;
import ru.volodin.deal.entity.jsonb.StatusHistory;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "statements")
@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Statement {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "statement_id")
    private UUID statementId;

    @OneToOne
    @JoinColumn(name = "client_id", referencedColumnName = "client_id")
    private Client client;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "credit_id", referencedColumnName = "credit_id")
    private Credit credit;

    @Enumerated(EnumType.STRING)
    @Column(name = "application_status")
    private ApplicationStatus status;

    @Column(name = "creation_date", columnDefinition = "timestamp")
    private LocalDateTime creationDate;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "applied_offer", columnDefinition = "jsonb")
    private LoanOfferDto appliedOffer;

    @Column(name = "sign_date", columnDefinition = "timestamp")
    private LocalDateTime signDate;

    @Column(name = "ses_code")
    private String code;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "status_history", columnDefinition = "jsonb")
    private List<StatusHistory> statusHistory;

    public void setStatus(ApplicationStatus status, ChangeType type) {
        this.status = status;
        addStatusHistory(StatusHistory.builder()
                .status(status.name())
                .time(LocalDateTime.now())
                .type(type)
                .build());
    }

    public void addStatusHistory(StatusHistory status) {
        if (statusHistory == null) {
            statusHistory = new ArrayList<>();
        }
        statusHistory.add(status);
    }
}
