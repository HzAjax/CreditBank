package ru.volodin.deal.entity.jsonb;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import ru.volodin.deal.entity.dto.enums.ApplicationStatus;
import ru.volodin.deal.entity.dto.enums.ChangeType;

import java.time.LocalDateTime;

@Data
public class StatusHistoryDto {
    private ApplicationStatus status;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime time;
    private ChangeType changeType;
}