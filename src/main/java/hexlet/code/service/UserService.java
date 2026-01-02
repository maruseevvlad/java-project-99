package hexlet.code.service;

import hexlet.code.dto.UserCreateDto;
import hexlet.code.dto.UserUpdateDto;
import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User createUser(UserCreateDto data) {
        User user = new User();
        user.setFirstName(data.firstName());
        user.setLastName(data.lastName());
        user.setEmail(data.email());
        user.setPassword(passwordEncoder.encode(data.password()));
        return userRepository.save(user);
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }

    public User updateUser(Long id, UserUpdateDto data) {
        User user = findById(id);
        if (data.firstName() != null) {
            user.setFirstName(data.firstName());
        }
        if (data.lastName() != null) {
            user.setLastName(data.lastName());
        }
        if (data.email() != null) {
            user.setEmail(data.email());
        }
        if (data.password() != null) {
            user.setPassword(passwordEncoder.encode(data.password()));
        }
        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
        User user = findById(id);
        userRepository.delete(user);
    }
}
