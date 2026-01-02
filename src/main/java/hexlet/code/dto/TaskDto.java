package hexlet.code.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import hexlet.code.model.Label;
import hexlet.code.model.Task;
import java.util.Set;

public record TaskDto(Long id,
                      Integer index,
                      String createdAt,
                      @JsonProperty("assignee_id") Long assigneeId,
                      String title,
                      String content,
                      String status,
                      @JsonProperty("label_ids") Set<Long> labelIds) {

    public static TaskDto fromEntity(Task task) {
        Long assigneeId = task.getAssignee() != null ? task.getAssignee().getId() : null;
        Set<Long> labels = task.getLabels().stream()
                .map(Label::getId)
                .collect(java.util.stream.Collectors.toSet());
        return new TaskDto(
                task.getId(),
                task.getIndex(),
                task.getCreatedAt() != null ? task.getCreatedAt().toString() : null,
                assigneeId,
                task.getTitle(),
                task.getContent(),
                task.getTaskStatus().getSlug(),
                labels
        );
    }
}
