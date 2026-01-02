package hexlet.code.service;

import hexlet.code.dto.TaskCreateDto;
import hexlet.code.dto.TaskUpdateDto;
import hexlet.code.model.Label;
import hexlet.code.model.Task;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class TaskService {
    private final TaskRepository taskRepository;
    private final TaskStatusRepository taskStatusRepository;
    private final UserRepository userRepository;
    private final LabelRepository labelRepository;

    public TaskService(TaskRepository taskRepository,
                       TaskStatusRepository taskStatusRepository,
                       UserRepository userRepository,
                       LabelRepository labelRepository) {
        this.taskRepository = taskRepository;
        this.taskStatusRepository = taskStatusRepository;
        this.userRepository = userRepository;
        this.labelRepository = labelRepository;
    }

    public Task create(TaskCreateDto data) {
        Task task = new Task();
        applyChanges(task, data.index(), data.assigneeId(), data.title(), data.content(), data.status(), data.labelIds());
        return taskRepository.save(task);
    }

    public List<Task> findAll() {
        return taskRepository.findAll();
    }

    public Task findById(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));
    }

    public Task update(Long id, TaskUpdateDto data) {
        Task task = findById(id);
        applyChanges(task, data.index(), data.assigneeId(), data.title(), data.content(), data.status(), data.labelIds());
        return taskRepository.save(task);
    }

    public void delete(Long id) {
        Task task = findById(id);
        taskRepository.delete(task);
    }

    private void applyChanges(Task task,
                              Integer index,
                              Long assigneeId,
                              String title,
                              String content,
                              String status,
                              List<Long> labelIds) {
        if (index != null) {
            task.setIndex(index);
        }
        if (title != null) {
            task.setTitle(title);
        }
        if (content != null) {
            task.setContent(content);
        }
        if (status != null) {
            TaskStatus taskStatus = taskStatusRepository.findBySlug(status)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task status not found"));
            task.setTaskStatus(taskStatus);
        }
        if (assigneeId != null) {
            User assignee = userRepository.findById(assigneeId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Assignee not found"));
            task.setAssignee(assignee);
        }
        if (labelIds != null) {
            Set<Label> labels = new HashSet<>(labelRepository.findAllById(labelIds));
            if (labels.size() != labelIds.size()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Label not found");
            }
            task.setLabels(labels);
        }
    }
}
