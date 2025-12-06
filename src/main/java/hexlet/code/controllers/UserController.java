package hexlet.code.controllers;

import hexlet.code.dto.*;
import hexlet.code.models.User;
import hexlet.code.services.UserService;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public UserResponseDto createUser(@Valid @RequestBody UserCreateDto dto) {
        User user = userService.createUser(dto);
        return mapToResponse(user);
    }

    @GetMapping("/{id}")
    public UserResponseDto getUser(@PathVariable Long id) {
        return mapToResponse(userService.getUser(id));
    }

    @GetMapping
    public List<UserResponseDto> getAllUsers() {
        return userService.getAllUsers().stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    @PutMapping("/{id}")
    public UserResponseDto updateUser(@PathVariable Long id, @Valid @RequestBody UserUpdateDto dto) {
        User updated = userService.updateUser(id, dto);
        return mapToResponse(updated);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }

    private UserResponseDto mapToResponse(User user) {
        UserResponseDto dto = new UserResponseDto();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setCreatedAt(user.getCreatedAt());
        return dto;
    }
}
