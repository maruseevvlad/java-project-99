package hexlet.code.service;

import hexlet.code.dto.TaskStatusCreateDTO;
import hexlet.code.dto.TaskStatusDTO;
import hexlet.code.dto.TaskStatusUpdateDTO;
import hexlet.code.model.TaskStatus;

import java.util.List;

public interface TaskStatusService {
    List<TaskStatusDTO> getAll();
    TaskStatusDTO create(TaskStatusCreateDTO taskStatusCreateDTO);
    TaskStatusDTO findById(Long id);
    TaskStatus findByIdEntity(Long id);
    TaskStatusDTO update(Long id, TaskStatusUpdateDTO taskStatusUpdateDTO);
    void delete(Long id);
    TaskStatus findBySlug(String slug);
}
