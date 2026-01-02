package hexlet.code.config;

import hexlet.code.model.User;
import hexlet.code.model.TaskStatus;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {
    private static final String DEFAULT_EMAIL = "hexlet@example.com";
    private static final String DEFAULT_PASSWORD = "qwerty";

    @Bean
    public CommandLineRunner seedAdminUser(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> userRepository.findByEmail(DEFAULT_EMAIL)
                .orElseGet(() -> {
                    User admin = new User();
                    admin.setEmail(DEFAULT_EMAIL);
                    admin.setPassword(passwordEncoder.encode(DEFAULT_PASSWORD));
                    admin.setFirstName("Hexlet");
                    admin.setLastName("Admin");
                    return userRepository.save(admin);
                });
    }

    @Bean
    public CommandLineRunner seedTaskStatuses(TaskStatusRepository taskStatusRepository) {
        return args -> {
            createStatusIfMissing(taskStatusRepository, "Draft", "draft");
            createStatusIfMissing(taskStatusRepository, "To review", "to_review");
            createStatusIfMissing(taskStatusRepository, "To be fixed", "to_be_fixed");
            createStatusIfMissing(taskStatusRepository, "To publish", "to_publish");
            createStatusIfMissing(taskStatusRepository, "Published", "published");
        };
    }

    private void createStatusIfMissing(TaskStatusRepository repository, String name, String slug) {
        repository.findBySlug(slug).orElseGet(() -> {
            TaskStatus status = new TaskStatus();
            status.setName(name);
            status.setSlug(slug);
            return repository.save(status);
        });
    }
}
