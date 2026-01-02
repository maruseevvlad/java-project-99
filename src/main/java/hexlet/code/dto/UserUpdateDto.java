package hexlet.code.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record UserUpdateDto(
        @Nullable String firstName,
        @Nullable String lastName,
        @Nullable @Email String email,
        @Nullable @Size(min = 3) String password
) { }
