package hexlet.code.dto;

import hexlet.code.model.Label;

public record LabelDto(Long id, String name, String createdAt) {
    public static LabelDto fromEntity(Label label) {
        return new LabelDto(
                label.getId(),
                label.getName(),
                label.getCreatedAt() != null ? label.getCreatedAt().toString() : null
        );
    }
}
