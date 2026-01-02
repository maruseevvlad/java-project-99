package hexlet.code.controller;

import hexlet.code.dto.TaskCreateDto;
import hexlet.code.dto.TaskDto;
import hexlet.code.dto.TaskParams;
import hexlet.code.dto.TaskUpdateDto;
import hexlet.code.model.Task;
import hexlet.code.service.TaskService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {
    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    public List<TaskDto> index(@RequestParam(required = false) String titleCont,
                               @RequestParam(required = false) Long assigneeId,
                               @RequestParam(required = false) String status,
                               @RequestParam(required = false) Long labelId) {
        TaskParams params = new TaskParams(titleCont, assigneeId, status, labelId);

        return taskService.findAll(params).stream()
                .map(TaskDto::fromEntity)
                .toList();
    }

    @GetMapping("/{id}")
    public TaskDto show(@PathVariable Long id) {
        Task task = taskService.findById(id);
        return TaskDto.fromEntity(task);
    }

    @PostMapping
    public ResponseEntity<TaskDto> create(@Valid @RequestBody TaskCreateDto data) {
        Task task = taskService.create(data);
        return ResponseEntity.status(HttpStatus.CREATED).body(TaskDto.fromEntity(task));
    }

    @PutMapping("/{id}")
    public TaskDto update(@PathVariable Long id, @Valid @RequestBody TaskUpdateDto data) {
        Task task = taskService.update(id, data);
        return TaskDto.fromEntity(task);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        taskService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
