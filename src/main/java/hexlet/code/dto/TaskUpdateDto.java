package hexlet.code.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Size;

public record TaskUpdateDto(
        Integer index,
        @JsonProperty("assignee_id") Long assigneeId,
        @Size(min = 1) String title,
        String content,
        @Size(min = 1) String status
) { }
