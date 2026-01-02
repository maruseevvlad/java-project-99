package hexlet.code.controller;

import hexlet.code.dto.TaskStatusCreateDto;
import hexlet.code.dto.TaskStatusDto;
import hexlet.code.dto.TaskStatusUpdateDto;
import hexlet.code.model.TaskStatus;
import hexlet.code.service.TaskStatusService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/task_statuses")
public class TaskStatusController {
    private final TaskStatusService taskStatusService;

    public TaskStatusController(TaskStatusService taskStatusService) {
        this.taskStatusService = taskStatusService;
    }

    @GetMapping
    public List<TaskStatusDto> index() {
        return taskStatusService.findAll()
                .stream()
                .map(TaskStatusDto::fromEntity)
                .toList();
    }

    @GetMapping("/{id}")
    public TaskStatusDto show(@PathVariable Long id) {
        TaskStatus taskStatus = taskStatusService.findById(id);
        return TaskStatusDto.fromEntity(taskStatus);
    }

    @PostMapping
    public ResponseEntity<TaskStatusDto> create(@Valid @RequestBody TaskStatusCreateDto data) {
        TaskStatus taskStatus = taskStatusService.create(data);
        return ResponseEntity.status(HttpStatus.CREATED).body(TaskStatusDto.fromEntity(taskStatus));
    }

    @PutMapping("/{id}")
    public TaskStatusDto update(@PathVariable Long id, @Valid @RequestBody TaskStatusUpdateDto data) {
        TaskStatus taskStatus = taskStatusService.update(id, data);
        return TaskStatusDto.fromEntity(taskStatus);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        taskStatusService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
