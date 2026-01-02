package hexlet.code.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserCreateDto(
        String firstName,
        String lastName,
        @NotBlank @Email String email,
        @NotBlank @Size(min = 3) String password
) { }
