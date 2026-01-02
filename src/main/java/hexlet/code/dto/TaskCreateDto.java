package hexlet.code.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record TaskCreateDto(
        Integer index,
        @JsonProperty("assignee_id") Long assigneeId,
        @NotBlank @Size(min = 1) String title,
        String content,
        @NotBlank @Size(min = 1) String status
) { }
