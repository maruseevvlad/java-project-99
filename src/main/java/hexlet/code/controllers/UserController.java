package hexlet.code.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import hexlet.code.dto.*;
import hexlet.code.services.UserService;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService service;

    @GetMapping
    public List<UserResponseDto> getAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public UserResponseDto getById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping
    public UserResponseDto create(@Valid @RequestBody UserCreateDto dto) {
        return service.create(dto);
    }

    @PutMapping("/{id}")
    public UserResponseDto update(
        @PathVariable Long id,
        @Valid @RequestBody UserUpdateDto dto
    ) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
