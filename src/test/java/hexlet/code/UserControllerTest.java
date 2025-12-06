package hexlet.code;

import hexlet.code.models.User;
import hexlet.code.repositories.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void clearDB() {
        userRepository.deleteAll();
    }

    @Test
    void testCreateUser() throws Exception {
        var json = """
                {
                    "email": "test@mail.com",
                    "password": "123456",
                    "firstName": "Test",
                    "lastName": "User"
                }
                """;

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.email").value("test@mail.com"))
            .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    void testCreateUserValidationFail() throws Exception {
        var invalidJson = """
                {
                    "email": "wrong-email",
                    "password": "1"
                }
                """;

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson))
            .andExpect(status().isBadRequest());
    }

    @Test
    void testGetUser() throws Exception {
        User user = new User();
        user.setEmail("john@example.com");
        user.setPassword("encoded_pass");
        userRepository.save(user);

        mockMvc.perform(get("/api/users/" + user.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.email").value("john@example.com"))
            .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    void testGetAllUsers() throws Exception {
        User user1 = new User();
        user1.setEmail("user1@mail.com");
        user1.setPassword("encoded");
        userRepository.save(user1);

        User user2 = new User();
        user2.setEmail("user2@mail.com");
        user2.setPassword("encoded");
        userRepository.save(user2);

        mockMvc.perform(get("/api/users"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].email").exists())
            .andExpect(jsonPath("$[1].email").exists());
    }

    @Test
    void testUpdateUserPartial() throws Exception {
        User user = new User();
        user.setEmail("old@mail.com");
        user.setPassword("pass");
        userRepository.save(user);

        var json = """
                {
                    "email": "new@mail.com"
                }
                """;

        mockMvc.perform(put("/api/users/" + user.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.email").value("new@mail.com"));
    }

    @Test
    void testDeleteUser() throws Exception {
        User user = new User();
        user.setEmail("delete@mail.com");
        user.setPassword("pass");
        userRepository.save(user);

        mockMvc.perform(delete("/api/users/" + user.getId()))
            .andExpect(status().isOk());

        Assertions.assertTrue(userRepository.findById(user.getId()).isEmpty());
    }
}
