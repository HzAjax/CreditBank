package ru.volodin.deal.entity.dto.api;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

}
