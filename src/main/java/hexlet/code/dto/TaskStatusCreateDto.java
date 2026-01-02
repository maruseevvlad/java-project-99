package hexlet.code.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record TaskStatusCreateDto(
        @NotBlank @Size(min = 1) String name,
        @NotBlank @Size(min = 1) String slug
) { }
