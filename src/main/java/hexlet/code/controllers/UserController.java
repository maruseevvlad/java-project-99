package hexlet.code.controllers;

import hexlet.code.config.SecurityUtils;
import hexlet.code.dto.*;
import hexlet.code.models.User;
import hexlet.code.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<UserResponseDto> createUser(@Valid @RequestBody UserCreateDto dto) {
        String currentUserEmail = SecurityUtils.getCurrentUserEmail();
        if (!SecurityUtils.isAdmin(currentUserEmail)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        User user = userService.createUser(dto);
        return ResponseEntity.ok(mapToResponse(user));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getUser(@PathVariable Long id) {
        try {
            User user = userService.getUser(id);
            return ResponseEntity.ok(mapToResponse(user));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping
    public ResponseEntity<List<UserResponseDto>> getAllUsers() {
        String currentUserEmail = SecurityUtils.getCurrentUserEmail();
        if (!SecurityUtils.isAdmin(currentUserEmail)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        List<UserResponseDto> users = userService.getAllUsers().stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDto> updateUser(@PathVariable Long id, @Valid @RequestBody UserUpdateDto dto) {
        String currentUserEmail = SecurityUtils.getCurrentUserEmail();
        try {
            User existingUser = userService.getUser(id);
            if (!existingUser.getEmail().equals(currentUserEmail) && !SecurityUtils.isAdmin(currentUserEmail)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            User updated = userService.updateUser(id, dto);
            return ResponseEntity.ok(mapToResponse(updated));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        String currentUserEmail = SecurityUtils.getCurrentUserEmail();
        try {
            User existingUser = userService.getUser(id);
            if (!existingUser.getEmail().equals(currentUserEmail) && !SecurityUtils.isAdmin(currentUserEmail)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            userService.deleteUser(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
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
