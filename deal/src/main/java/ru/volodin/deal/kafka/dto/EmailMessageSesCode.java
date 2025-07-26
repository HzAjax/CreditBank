package ru.volodin.deal.kafka.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.volodin.deal.kafka.dto.enums.Theme;

import java.util.UUID;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmailMessageSesCode {
    private String address;
    private Theme theme;
    private UUID statementId;
    private UUID sesCodeConfirm;
}
