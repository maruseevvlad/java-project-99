package hexlet.code.dto;

import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.*;

@Getter
@Setter
public class UserCreateDto {

    @NotBlank
    @Email
    private String email;

    private String firstName;
    private String lastName;

    @NotBlank
    @Size(min = 3)
    private String password;
}
