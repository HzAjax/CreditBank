package ru.volodin.deal.entity.jsonb;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import ru.volodin.deal.entity.dto.enums.ApplicationStatus;
import ru.volodin.deal.entity.dto.enums.ChangeType;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StatusHistory {
    private String status;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime time;
    private ChangeType type;
}