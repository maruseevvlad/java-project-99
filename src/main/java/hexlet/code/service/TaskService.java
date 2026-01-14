package hexlet.code.service;

import hexlet.code.dto.TaskCreateDTO;
import hexlet.code.dto.TaskDTO;
import hexlet.code.dto.TaskUpdateDTO;

import java.util.List;

public interface TaskService {
    List<TaskDTO> getAll();
    List<TaskDTO> getWithFilters(String titleCont, Long assigneeId, String status, Long labelId);
    TaskDTO create(TaskCreateDTO taskCreateDTO);
    TaskDTO findById(Long id);
    TaskDTO update(Long id, TaskUpdateDTO taskUpdateDTO);
    void delete(Long id);
}
