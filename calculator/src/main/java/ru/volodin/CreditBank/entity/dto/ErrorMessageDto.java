package ru.volodin.CreditBank.entity.dto;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ErrorMessageDto {
    private String message;
}
