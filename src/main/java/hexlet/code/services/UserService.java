package hexlet.code.services;

import hexlet.code.dto.*;
import hexlet.code.models.User;
import hexlet.code.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repo;
    private final BCryptPasswordEncoder encoder;

    public UserResponseDto create(UserCreateDto dto) {
        User user = new User();
        user.setEmail(dto.getEmail());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setPassword(encoder.encode(dto.getPassword()));

        repo.save(user);
        return toResponse(user);
    }

    public UserResponseDto findById(Long id) {
        User user = repo.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found"));
        return toResponse(user);
    }

    public List<UserResponseDto> findAll() {
        return repo.findAll().stream()
            .map(this::toResponse)
            .toList();
    }

    public UserResponseDto update(Long id, UserUpdateDto dto) {
        User user = repo.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found"));

        if (dto.getEmail() != null) user.setEmail(dto.getEmail());
        if (dto.getFirstName() != null) user.setFirstName(dto.getFirstName());
        if (dto.getLastName() != null) user.setLastName(dto.getLastName());
        if (dto.getPassword() != null) user.setPassword(encoder.encode(dto.getPassword()));

        repo.save(user);
        return toResponse(user);
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }

    private UserResponseDto toResponse(User user) {
        UserResponseDto dto = new UserResponseDto();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setCreatedAt(user.getCreatedAt());
        return dto;
    }
}
