package ru.volodin.deal.entity.jsonb;

import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Builder
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Passport {
    private UUID passportUUID;
    private String series;
    private String number;
    private String issueBranch;
    private LocalDate issueDate;
}
