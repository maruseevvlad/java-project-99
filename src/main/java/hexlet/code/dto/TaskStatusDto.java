package hexlet.code.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import hexlet.code.model.TaskStatus;
import java.time.LocalDate;

public record TaskStatusDto(
        Long id,
        String name,
        String slug,
        @JsonProperty("createdAt") LocalDate createdAt
) {
    public static TaskStatusDto fromEntity(TaskStatus taskStatus) {
        return new TaskStatusDto(
                taskStatus.getId(),
                taskStatus.getName(),
                taskStatus.getSlug(),
                taskStatus.getCreatedAt()
        );
    }
}
