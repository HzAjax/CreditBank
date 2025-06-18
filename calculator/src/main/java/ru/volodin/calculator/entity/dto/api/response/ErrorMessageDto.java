package ru.volodin.calculator.entity.dto.api.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Schema(
        description = "DTO that describes error details returned in case of API failure or validation error"
)
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ErrorMessageDto {

    @Schema(
            description = "Human-readable error message",
            example = "term: Minimum term is 6 months"
    )
    private String message;

    @Schema(
            description = "HTTP status code",
            example = "400"
    )
    private int status;

    @Schema(
            description = "Request path where the error occurred",
            example = "/calculator/calc"
    )
    private String path;

    @Schema(
            description = "Timestamp of when the error occurred",
            example = "2025-06-17T10:38:14.8931083"
    )
    private LocalDateTime timestamp;
}
