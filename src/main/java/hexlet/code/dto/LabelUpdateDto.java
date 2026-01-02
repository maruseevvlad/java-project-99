package hexlet.code.dto;

import jakarta.validation.constraints.Size;

public record LabelUpdateDto(@Size(min = 3, max = 1000) String name) {
}
