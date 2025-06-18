package ru.volodin.deal.entity.jsonb;

import lombok.Data;

import java.time.LocalDate;

@Data
public class Passport {
    private String series;
    private String number;
    private String issueBranch;
    private LocalDate issueDate;
}
