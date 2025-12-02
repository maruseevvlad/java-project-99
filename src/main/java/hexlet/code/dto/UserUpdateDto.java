package hexlet.code.dto;

import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.*;

@Getter
@Setter
public class UserUpdateDto {

    @Email
    private String email;

    private String firstName;
    private String lastName;

    @Size(min = 3)
    private String password;
}
