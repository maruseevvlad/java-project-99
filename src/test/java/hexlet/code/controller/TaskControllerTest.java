package hexlet.code.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.model.Task;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void shouldRequireAuthForTasks() throws Exception {
        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldCreateTask() throws Exception {
        User assignee = createUser("john@example.com", "John", "Doe", "secret");

        Map<String, Object> params = new HashMap<>();
        params.put("index", 12);
        params.put("assignee_id", assignee.getId());
        params.put("title", "Test title");
        params.put("content", "Test content");
        params.put("status", "draft");

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(params))
                        .header(HttpHeaders.AUTHORIZATION, bearer(loginAsAdmin())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Test title"))
                .andExpect(jsonPath("$.assignee_id").value(assignee.getId()))
                .andExpect(jsonPath("$.status").value("draft"));

        assertThat(taskRepository.findAll()).hasSize(1);
    }

    @Test
    void shouldShowTask() throws Exception {
        Task task = createTask("Initial task", "draft");

        mockMvc.perform(get("/api/tasks/" + task.getId())
                        .header(HttpHeaders.AUTHORIZATION, bearer(loginAsAdmin())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Initial task"))
                .andExpect(jsonPath("$.status").value("draft"))
                .andExpect(jsonPath("$.createdAt").isNotEmpty());
    }

    @Test
    void shouldUpdateTask() throws Exception {
        Task task = createTask("Initial", "draft");

        Map<String, Object> params = new HashMap<>();
        params.put("title", "Updated title");
        params.put("content", "Updated content");
        params.put("status", "to_review");

        mockMvc.perform(put("/api/tasks/" + task.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(params))
                        .header(HttpHeaders.AUTHORIZATION, bearer(loginAsAdmin())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated title"))
                .andExpect(jsonPath("$.content").value("Updated content"))
                .andExpect(jsonPath("$.status").value("to_review"));
    }

    @Test
    void shouldDeleteTask() throws Exception {
        Task task = createTask("To delete", "draft");

        mockMvc.perform(delete("/api/tasks/" + task.getId())
                        .header(HttpHeaders.AUTHORIZATION, bearer(loginAsAdmin())))
                .andExpect(status().isNoContent());

        assertThat(taskRepository.findById(task.getId())).isEmpty();
    }

    private Task createTask(String title, String statusSlug) {
        TaskStatus status = taskStatusRepository.findBySlug(statusSlug).orElseThrow();
        Task task = new Task();
        task.setTitle(title);
        task.setTaskStatus(status);
        return taskRepository.save(task);
    }

    private User createUser(String email, String firstName, String lastName, String password) {
        User user = new User();
        user.setEmail(email);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setPassword(passwordEncoder.encode(password));
        return userRepository.save(user);
    }

    private String loginAsAdmin() throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("username", "hexlet@example.com");
        params.put("password", "qwerty");

        return mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(params)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
    }

    private String bearer(String token) {
        return "Bearer " + token;
    }
}
