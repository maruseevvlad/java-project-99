package hexlet.code.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import hexlet.code.model.User;
import java.time.LocalDateTime;

public record UserDto(
        Long id,
        String firstName,
        String lastName,
        String email,
        @JsonProperty("createdAt") LocalDateTime createdAt
) {
    public static UserDto fromEntity(User user) {
        return new UserDto(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getCreatedAt()
        );
    }
}
