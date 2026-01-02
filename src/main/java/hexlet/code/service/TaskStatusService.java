package hexlet.code.service;

import hexlet.code.dto.TaskStatusCreateDto;
import hexlet.code.dto.TaskStatusUpdateDto;
import hexlet.code.model.TaskStatus;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.TaskStatusRepository;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class TaskStatusService {
    private final TaskStatusRepository taskStatusRepository;
    private final TaskRepository taskRepository;

    public TaskStatusService(TaskStatusRepository taskStatusRepository, TaskRepository taskRepository) {
        this.taskStatusRepository = taskStatusRepository;
        this.taskRepository = taskRepository;
    }

    public TaskStatus create(TaskStatusCreateDto data) {
        TaskStatus taskStatus = new TaskStatus();
        taskStatus.setName(data.name());
        taskStatus.setSlug(data.slug());
        return taskStatusRepository.save(taskStatus);
    }

    public List<TaskStatus> findAll() {
        return taskStatusRepository.findAll();
    }

    public TaskStatus findById(Long id) {
        return taskStatusRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task status not found"));
    }

    public TaskStatus update(Long id, TaskStatusUpdateDto data) {
        TaskStatus taskStatus = findById(id);
        if (data.name() != null) {
            taskStatus.setName(data.name());
        }
        if (data.slug() != null) {
            taskStatus.setSlug(data.slug());
        }
        return taskStatusRepository.save(taskStatus);
    }

    public void delete(Long id) {
        TaskStatus taskStatus = findById(id);
        if (taskRepository.existsByTaskStatusId(taskStatus.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot delete status linked to tasks");
        }
        taskStatusRepository.delete(taskStatus);
    }
}
