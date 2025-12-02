package hexlet.code.init;

import hexlet.code.models.User;
import hexlet.code.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository repo;
    private final BCryptPasswordEncoder encoder;

    @Override
    public void run(String... args) {

        if (repo.findAll().isEmpty()) {
            User admin = new User();
            admin.setEmail("hexlet@example.com");
            admin.setPassword(encoder.encode("qwerty"));
            repo.save(admin);

            System.out.println("Admin user created!");
        }
    }
}
