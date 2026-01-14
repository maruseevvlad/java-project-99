package hexlet.code.mapper;

import hexlet.code.dto.TaskStatusCreateDTO;
import hexlet.code.dto.TaskStatusDTO;
import hexlet.code.dto.TaskStatusUpdateDTO;
import hexlet.code.model.TaskStatus;
import org.springframework.stereotype.Component;

@Component
public class TaskStatusMapper {

    public TaskStatusDTO map(TaskStatus taskStatus) {
        TaskStatusDTO taskStatusDTO = new TaskStatusDTO();
        taskStatusDTO.setId(taskStatus.getId());
        taskStatusDTO.setName(taskStatus.getName());
        taskStatusDTO.setSlug(taskStatus.getSlug());
        taskStatusDTO.setCreatedAt(taskStatus.getCreatedAt());
        return taskStatusDTO;
    }

    public TaskStatus map(TaskStatusCreateDTO taskStatusCreateDTO) {
        TaskStatus taskStatus = new TaskStatus();
        taskStatus.setName(taskStatusCreateDTO.getName());
        taskStatus.setSlug(taskStatusCreateDTO.getSlug());
        return taskStatus;
    }

    public void update(TaskStatusUpdateDTO taskStatusUpdateDTO, TaskStatus taskStatus) {
        if (taskStatusUpdateDTO.getName() != null) {
            taskStatus.setName(taskStatusUpdateDTO.getName());
        }
        if (taskStatusUpdateDTO.getSlug() != null) {
            taskStatus.setSlug(taskStatusUpdateDTO.getSlug());
        }
    }
}
