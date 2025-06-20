package ru.volodin.deal.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import ru.volodin.deal.entity.dto.api.PaymentScheduleElementDto;
import ru.volodin.deal.entity.dto.enums.CreditStatus;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "credits")
@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Credit {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "credit_id")
    private UUID creditId;

    @Column(name = "amount")
    private BigDecimal amount;

    @Column(name = "term")
    private Integer term;

    @Column(name = "monthly_payment")
    private BigDecimal monthlyPayment;

    @Column(name = "rate")
    private BigDecimal rate;

    @Column(name = "psk")
    private BigDecimal psk;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "payment_schedule")
    private List<PaymentScheduleElementDto> paymentSchedule;

    @Column(name = "insurance_enabled")
    private Boolean insuranceEnabled;

    @Column(name = "salary_client")
    private Boolean salaryClient;

    @Enumerated(EnumType.STRING)
    @Column(name = "credit_status")
    private CreditStatus status;
}
