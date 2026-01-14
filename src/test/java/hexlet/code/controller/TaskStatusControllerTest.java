package hexlet.code.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.AppApplication;
import hexlet.code.dto.TaskStatusDTO;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.TaskStatusRepository;
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
public class TaskStatusControllerTest {

    private static final String TEST_STATUS = "Test Status";
    private static final String TEST_1STATUS = "test_status";
    private static final String AUTH = "Authorization";
    private static final String BEARER = "Bearer ";
    private static final String API_TASK_1STATUSES_ID = "/api/task_statuses/{id}";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

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
        taskStatusRepository.deleteAll();
        userRepository.deleteAll();

        User user = new User();
        user.setEmail("admin@example.com");
        user.setPassword(passwordEncoder.encode("password"));
        user.setFirstName("Admin");
        user.setLastName("User");
        userRepository.save(user);

        token = getToken("admin@example.com", "password");
    }

    @Test
    void testIndex() throws Exception {
        TaskStatus status = new TaskStatus();
        status.setName(TEST_STATUS);
        status.setSlug(TEST_1STATUS);
        taskStatusRepository.save(status);

        MvcResult result = mockMvc.perform(get("/api/task_statuses")
                .header(AUTH, BEARER + token))
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        List<TaskStatusDTO> taskStatusDTOs
            = objectMapper.readValue(content, new TypeReference<List<TaskStatusDTO>>() { });

        List<TaskStatus> taskStatusesFromDb = taskStatusRepository.findAll();

        assertThat(taskStatusDTOs).hasSize(taskStatusesFromDb.size());

        List<String> statusNamesFromDb = taskStatusesFromDb.stream()
                .map(TaskStatus::getName)
                .toList();

        List<String> statusNamesFromResponse = taskStatusDTOs.stream()
                .map(TaskStatusDTO::getName)
                .toList();

        assertThat(statusNamesFromResponse).containsExactlyElementsOf(statusNamesFromDb);
    }

    @Test
    void testShow() throws Exception {
        TaskStatus status = new TaskStatus();
        status.setName(TEST_STATUS);
        status.setSlug(TEST_1STATUS);
        taskStatusRepository.save(status);

        mockMvc.perform(get(API_TASK_1STATUSES_ID, status.getId())
                .header(AUTH, BEARER + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(TEST_STATUS));
    }

    @Test
    void testCreate() throws Exception {
        String statusData = "{\"name\":\"New Status\",\"slug\":\"new_status\"}";
        mockMvc.perform(post("/api/task_statuses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(statusData)
                .header(AUTH, BEARER + token))
                .andExpect(status().isCreated());

        TaskStatus status = taskStatusRepository.findBySlug("new_status")
                .orElseThrow(() -> new AssertionError("TaskStatus not found"));

        assertThat(status.getName()).isEqualTo("New Status");
    }

    @Test
    void testUpdate() throws Exception {
        TaskStatus status = new TaskStatus();
        status.setName("Old Status");
        status.setSlug("old_status");
        taskStatusRepository.save(status);

        String updateData = "{\"name\":\"Updated Status\"}";

        mockMvc.perform(put(API_TASK_1STATUSES_ID, status.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateData)
                .header(AUTH, BEARER + token))
                .andExpect(status().isOk());

        TaskStatus updatedStatus = taskStatusRepository.findById(status.getId()).orElse(null);
        assertThat(updatedStatus).isNotNull();
        assertThat(updatedStatus.getName()).isEqualTo("Updated Status");
    }

    @Test
    void testDestroy() throws Exception {
        TaskStatus status = new TaskStatus();
        status.setName(TEST_STATUS);
        status.setSlug(TEST_1STATUS);
        taskStatusRepository.save(status);

        mockMvc.perform(delete(API_TASK_1STATUSES_ID, status.getId())
                .header(AUTH, BEARER + token))
                .andExpect(status().isNoContent());

        assertThat(taskStatusRepository.existsById(status.getId())).isFalse();
    }
}
