package hexlet.code.service;

import hexlet.code.dto.UserCreateDto;
import hexlet.code.dto.UserUpdateDto;
import hexlet.code.model.User;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.UserRepository;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import hexlet.code.security.CustomUserDetails;

@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TaskRepository taskRepository;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, TaskRepository taskRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.taskRepository = taskRepository;
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
        ensureOwnership(user.getId());
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
        ensureOwnership(user.getId());
        if (taskRepository.existsByAssigneeId(user.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot delete user linked to tasks");
        }
        userRepository.delete(user);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return new CustomUserDetails(user);
    }

    private void ensureOwnership(Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails details)) {
            throw new AccessDeniedException("Access denied");
        }

        if (!details.getId().equals(id)) {
            throw new AccessDeniedException("Access denied");
        }
    }
}
