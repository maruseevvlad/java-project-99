package hexlet.code.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Size;
import java.util.List;

public record TaskUpdateDto(
        Integer index,
        @JsonProperty("assignee_id") Long assigneeId,
        @Size(min = 1) String title,
        String content,
        @Size(min = 1) String status,
        @JsonProperty("label_ids") List<Long> labelIds
) { }
