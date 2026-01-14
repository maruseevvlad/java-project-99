package hexlet.code.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.AppApplication;
import hexlet.code.dto.UserDTO;
import hexlet.code.model.User;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.assertj.core.api.Assertions.assertThat;



@ActiveProfiles("test")
@SpringBootTest(classes = AppApplication.class)
@AutoConfigureMockMvc
class UserControllerTest {

    private static final String EMAIL = "admin@example.com";
    private static final String AUTH = "Authorization";
    private static final String BEARER = "Bearer ";
    private static final String API_USERS_ID = "/api/users/{id}";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    private String token;

    private String getToken(String email, String password) throws Exception {
        String credentials = "{\"username\":\"" + email + "\",\"password\":\"" + password + "\"}";
        MvcResult result = mockMvc.perform(post("/api/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(credentials))
                .andExpect(status().isOk())
                .andReturn();

        return result.getResponse().getContentAsString();
    }

    @BeforeEach
    void setUp() throws Exception {
        taskRepository.deleteAll();
        userRepository.deleteAll();
        User user = new User();
        user.setEmail(EMAIL);
        user.setPassword(passwordEncoder.encode("password"));
        user.setFirstName("Admin");
        user.setLastName("User");
        userRepository.save(user);

        token = getToken(EMAIL, "password");
    }

    @Test
    void testIndex() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/users")
                .header(AUTH, BEARER + token))
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        List<UserDTO> userDTOs = objectMapper.readValue(content, new TypeReference<List<UserDTO>>() { });

        List<User> usersFromDb = userRepository.findAll();

        assertThat(userDTOs).hasSize(usersFromDb.size());

        List<String> userEmailsFromDb = usersFromDb.stream()
                .map(User::getEmail)
                .toList();

        List<String> userEmailsFromResponse = userDTOs.stream()
                .map(UserDTO::getEmail)
                .toList();

        assertThat(userEmailsFromResponse).containsExactlyElementsOf(userEmailsFromDb);
    }

    @Test
    void testShow() throws Exception {
        User user = userRepository.findByEmail(EMAIL).get();

        mockMvc.perform(get(API_USERS_ID, user.getId())
                .header(AUTH, BEARER + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(EMAIL));
    }

    @Test
    void testCreate() throws Exception {
        String userData
            = "{\"email\":\"test@example.com\",\"password\":\"password\",\"firstName\":\"Test\",\"lastName\":\"User\"}";

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userData)
                .header(AUTH, BEARER + token))
                .andExpect(status().isCreated());

        User user = userRepository.findByEmail("test@example.com").get();
        assertThat(user).isNotNull();
    }

    @Test
    void testUpdate() throws Exception {
        User user = userRepository.findByEmail(EMAIL).get();
        String updateData = "{\"email\":\"new@example.com\",\"firstName\":\"New\",\"lastName\":\"Name\"}";

        mockMvc.perform(put(API_USERS_ID, user.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateData)
                .header(AUTH, BEARER + token))
                .andExpect(status().isOk());

        User updatedUser = userRepository.findById(user.getId()).get();
        assertThat(updatedUser.getEmail()).isEqualTo("new@example.com");
    }

    @Test
    void testDestroy() throws Exception {
        User user = userRepository.findByEmail(EMAIL).get();

        mockMvc.perform(delete(API_USERS_ID, user.getId())
                .header(AUTH, BEARER + token))
                .andExpect(status().isNoContent());

        assertThat(userRepository.existsById(user.getId())).isFalse();
    }
}
