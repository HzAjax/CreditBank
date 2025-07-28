package ru.volodin.dossier.kafka.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.volodin.dossier.kafka.dto.enums.Theme;

import java.util.UUID;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmailMessage {
    private String address;
    private Theme theme;
    private UUID statementId;
}
