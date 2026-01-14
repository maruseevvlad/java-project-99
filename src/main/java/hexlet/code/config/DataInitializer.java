package hexlet.code.config;

import hexlet.code.model.Task;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import hexlet.code.model.Label;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import lombok.AllArgsConstructor;
import hexlet.code.repository.LabelRepository;
import java.util.Optional;
import java.util.Set;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private static final String EMAIL = "hexlet@example.com";
    private final UserRepository userRepository;
    private final TaskStatusRepository taskStatusRepository;
    private final TaskRepository taskRepository;
    private final LabelRepository labelRepository;
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        User user = null;
        Optional<User> optionalUser = userRepository.findByEmail(EMAIL);

        if (optionalUser.isEmpty()) {
            user = new User();
            user.setEmail(EMAIL);
            user.setPassword(passwordEncoder.encode("qwerty"));
            user.setFirstName("Hexlet");
            user.setLastName("User");
            user = userRepository.save(user);
        } else {
            user = optionalUser.get();
        }

        TaskStatus draft = addDefaultStatus("Draft", "draft");
        TaskStatus toReview = addDefaultStatus("ToReview", "to_review");
        TaskStatus toPublish = addDefaultStatus("ToPublish", "to_publish");

        Label bug = addDefaultLabel("bug");
        Label feature = addDefaultLabel("feature");
        Set<Label> labels = Set.of(bug, feature);

        if (taskRepository.count() == 0) {
            Task task1 = new Task();
            task1.setTitle("First task");
            task1.setIndex(1);
            task1.setContent("This is the first task");
            task1.setTaskStatus(draft);
            task1.setAssignee(user);
            taskRepository.save(task1);

            Task task2 = new Task();
            task2.setTitle("Second task");
            task2.setIndex(2);
            task2.setContent("This is the second task");
            task2.setTaskStatus(toReview);
            task2.setAssignee(user);
            taskRepository.save(task2);

            Task task3 = new Task();
            task3.setTitle("Third task");
            task3.setIndex(3);
            task3.setContent("This is the third task");
            task3.setTaskStatus(toPublish);
            task3.setAssignee(user);
            task3.setLabels(labels);
            taskRepository.save(task3);
        }
    }

    private TaskStatus addDefaultStatus(String name, String slug) {
        return taskStatusRepository.findBySlug(slug).orElseGet(() -> {
            TaskStatus status = new TaskStatus();
            status.setName(name);
            status.setSlug(slug);
            return taskStatusRepository.save(status);
        });
    }

    private Label addDefaultLabel(String name) {
        return labelRepository.findByName(name).orElseGet(() -> {
            Label label = new Label();
            label.setName(name);
            return labelRepository.save(label);
        });
    }
}
