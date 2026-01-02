package hexlet.code.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LabelCreateDto(@NotBlank @Size(min = 3, max = 1000) String name) {
}
