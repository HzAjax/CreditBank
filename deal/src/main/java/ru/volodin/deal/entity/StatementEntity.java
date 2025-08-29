package ru.volodin.deal.entity;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(
        description = "Loan offer DTO containing possible loan terms calculated based on the client's request"
)
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
    @Schema(
            description = "The unique identifier of the statement",
            example = "7f73b490-0610-47ac-8753-fc2d9e0aaf1e"
    )
    private UUID statementId;

    @OneToOne
    @JoinColumn(name = "client_id", referencedColumnName = "client_id")
    @Schema(description = "Information about the client")
    private ClientEntity client;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "credit_id", referencedColumnName = "credit_id")
    @Schema(description = "Information about the credit")
    private CreditEntity credit;

    @Enumerated(EnumType.STRING)
    @Column(name = "application_status")
    @Schema(
            description = "Current status of the statement",
            example = "APPROVED"
    )
    private ApplicationStatus status;

    @Column(name = "creation_date", columnDefinition = "timestamp")
    @Schema(
            description = "Date and time when the statement was created",
            example = "2025-07-31 14:48:54.920"
    )
    private LocalDateTime creationDate;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "applied_offer")
    @Schema(description = "Selected loan offer")
    private LoanOfferDto appliedOffer;

    @Column(name = "sign_date", columnDefinition = "timestamp")
    @Schema(
            description = "Date and time of signing the documents",
            example = "2025-07-31 18:44:42.912"
    )
    private LocalDateTime signDate;

    @Column(name = "ses_code")
    @Schema(
            description = "SES code for signing documents",
            example = "d0ad85"
    )
    private String code;

    @Builder.Default
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "status_history")
    @Schema(description = "The history of changing statuses statement")
    private List<StatusHistory> statusHistory = new ArrayList<>();

    public void addStatusHistory(StatusHistory status) {
        statusHistory.add(status);
    }
}
