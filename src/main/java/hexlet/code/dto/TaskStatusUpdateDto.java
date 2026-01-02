package hexlet.code.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Size;

public record TaskStatusUpdateDto(
        @Nullable @Size(min = 1) String name,
        @Nullable @Size(min = 1) String slug
) { }
