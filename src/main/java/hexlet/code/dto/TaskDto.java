package hexlet.code.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import hexlet.code.model.Task;

public record TaskDto(Long id,
                      Integer index,
                      String createdAt,
                      @JsonProperty("assignee_id") Long assigneeId,
                      String title,
                      String content,
                      String status) {

    public static TaskDto fromEntity(Task task) {
        Long assigneeId = task.getAssignee() != null ? task.getAssignee().getId() : null;
        return new TaskDto(
                task.getId(),
                task.getIndex(),
                task.getCreatedAt() != null ? task.getCreatedAt().toString() : null,
                assigneeId,
                task.getTitle(),
                task.getContent(),
                task.getTaskStatus().getSlug()
        );
    }
}
