package hexlet.code.config;

import hexlet.code.models.User;
import hexlet.code.repositories.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder encoder;

    public DataInitializer(UserRepository userRepository, PasswordEncoder encoder) {
        this.userRepository = userRepository;
        this.encoder = encoder;
    }

    @Override
    public void run(String... args) {
        if (userRepository.findAll().isEmpty()) {
            User admin = new User();
            admin.setEmail("hexlet@example.com");
            admin.setPassword(encoder.encode("qwerty"));
            userRepository.save(admin);
        }
    }
}
