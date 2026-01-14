package hexlet.code.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.AppApplication;
import hexlet.code.dto.TaskDTO;
import hexlet.code.model.Task;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import hexlet.code.model.Label;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import hexlet.code.repository.LabelRepository;
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
public class TaskControllerTest {

    private static final String PWD = "password";
    private static final String TEST_TASK = "Test Task";
    private static final String TEST_CONTENT = "Test Content";
    private static final String AUTH = "Authorization";
    private static final String BEARER = "Bearer ";
    private static final String TITLE = "$[0].title";
    private static final String API_TASKS_ID = "/api/tasks/{id}";
    private static final String TASK_1 = "Task 1";
    private static final String TASK_2 = "Task 2";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LabelRepository labelRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    private String token;
    private User testUser;
    private TaskStatus testStatus;
    private Label testLabel;

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
        labelRepository.deleteAll();

        testUser = new User();
        testUser.setEmail("admin@example.com");
        testUser.setPassword(passwordEncoder.encode(PWD));
        testUser.setFirstName("Admin");
        testUser.setLastName("User");
        userRepository.save(testUser);

        testStatus = new TaskStatus();
        testStatus.setName("Test Status");
        testStatus.setSlug("test_status");
        taskStatusRepository.save(testStatus);

        testLabel = new Label();
        testLabel.setName("Test Label");
        labelRepository.save(testLabel);

        token = getToken("admin@example.com", PWD);
    }

    @Test
    void testIndex() throws Exception {
        Task task = new Task();
        task.setTitle(TEST_TASK);
        task.setContent(TEST_CONTENT);
        task.setTaskStatus(testStatus);
        task.setAssignee(testUser);
        taskRepository.save(task);

        MvcResult result = mockMvc.perform(get("/api/tasks")
                .header(AUTH, BEARER + token))
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        List<TaskDTO> taskDTOs = objectMapper.readValue(content, new TypeReference<List<TaskDTO>>() { });

        List<Task> tasksFromDb = taskRepository.findAll();

        assertThat(taskDTOs).hasSize(tasksFromDb.size());

        List<String> taskTitlesFromDb = tasksFromDb.stream()
                .map(Task::getTitle)
                .toList();

        List<String> taskTitlesFromResponse = taskDTOs.stream()
                .map(TaskDTO::getTitle)
                .toList();

        assertThat(taskTitlesFromResponse).containsExactlyElementsOf(taskTitlesFromDb);
    }

    @Test
    void testShow() throws Exception {
        Task task = new Task();
        task.setTitle(TEST_TASK);
        task.setContent(TEST_CONTENT);
        task.setTaskStatus(testStatus);
        task.setAssignee(testUser);
        taskRepository.save(task);

        mockMvc.perform(get(API_TASKS_ID, task.getId())
                .header(AUTH, BEARER + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value(TEST_TASK));
    }

    @Test
    void testCreate() throws Exception {
        String taskData = "{\"title\":\"New Task\",\"content\":\"New Content\",\"status\":\""
                + testStatus.getSlug() + "\",\"assigneeId\":" + testUser.getId() + "}";

        mockMvc.perform(post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(taskData)
                .header(AUTH, BEARER + token))
                .andExpect(status().isCreated());

        Task task = taskRepository.findAll().get(0);
        assertThat(task).isNotNull();
        assertThat(task.getTitle()).isEqualTo("New Task");
    }

    @Test
    void testUpdate() throws Exception {
        Task task = new Task();
        task.setTitle("Old Task");
        task.setContent("Old Content");
        task.setTaskStatus(testStatus);
        task.setAssignee(testUser);
        taskRepository.save(task);

        String updateData = "{\"title\":\"Updated Task\"}";

        mockMvc.perform(put(API_TASKS_ID, task.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateData)
                .header(AUTH, BEARER + token))
                .andExpect(status().isOk());

        Task updatedTask = taskRepository.findById(task.getId()).orElse(null);
        assertThat(updatedTask).isNotNull();
        assertThat(updatedTask.getTitle()).isEqualTo("Updated Task");
    }

    @Test
    void testDestroy() throws Exception {
        Task task = new Task();
        task.setTitle(TEST_TASK);
        task.setContent(TEST_CONTENT);
        task.setTaskStatus(testStatus);
        task.setAssignee(testUser);
        taskRepository.save(task);

        mockMvc.perform(delete(API_TASKS_ID, task.getId())
                .header(AUTH, BEARER + token))
                .andExpect(status().isNoContent());

        assertThat(taskRepository.existsById(task.getId())).isFalse();
    }

    @Test
    void testFilterByTitle() throws Exception {
        Task task1 = new Task();
        task1.setTitle("Create new feature");
        task1.setContent(TEST_CONTENT);
        task1.setTaskStatus(testStatus);
        task1.setAssignee(testUser);
        taskRepository.save(task1);

        Task task2 = new Task();
        task2.setTitle("Fix bug");
        task2.setContent(TEST_CONTENT);
        task2.setTaskStatus(testStatus);
        task2.setAssignee(testUser);
        taskRepository.save(task2);

        mockMvc.perform(get("/api/tasks?titleCont=feature")
                .header(AUTH, BEARER + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath(TITLE).value("Create new feature"))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").value(org.hamcrest.Matchers.hasSize(1)));
    }

    @Test
    void testFilterByAssignee() throws Exception {
        Task task1 = new Task();
        task1.setTitle(TASK_1);
        task1.setContent(TEST_CONTENT);
        task1.setTaskStatus(testStatus);
        task1.setAssignee(testUser);
        taskRepository.save(task1);

        User anotherUser = new User();
        anotherUser.setEmail("another@example.com");
        anotherUser.setPassword(passwordEncoder.encode(PWD));
        anotherUser.setFirstName("Another");
        anotherUser.setLastName("User");
        userRepository.save(anotherUser);

        Task task2 = new Task();
        task2.setTitle(TASK_2);
        task2.setContent(TEST_CONTENT);
        task2.setTaskStatus(testStatus);
        task2.setAssignee(anotherUser);
        taskRepository.save(task2);

        mockMvc.perform(get("/api/tasks?assigneeId=" + testUser.getId())
                .header(AUTH, BEARER + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath(TITLE).value(TASK_1))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").value(org.hamcrest.Matchers.hasSize(1)));
    }

    @Test
    void testFilterByStatus() throws Exception {
        TaskStatus anotherStatus = new TaskStatus();
        anotherStatus.setName("Another Status");
        anotherStatus.setSlug("another_status");
        taskStatusRepository.save(anotherStatus);

        Task task1 = new Task();
        task1.setTitle(TASK_1);
        task1.setContent(TEST_CONTENT);
        task1.setTaskStatus(testStatus);
        task1.setAssignee(testUser);
        taskRepository.save(task1);

        Task task2 = new Task();
        task2.setTitle(TASK_2);
        task2.setContent(TEST_CONTENT);
        task2.setTaskStatus(anotherStatus);
        task2.setAssignee(testUser);
        taskRepository.save(task2);

        mockMvc.perform(get("/api/tasks?status=" + testStatus.getSlug())
                .header(AUTH, BEARER + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath(TITLE).value(TASK_1))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").value(org.hamcrest.Matchers.hasSize(1)));
    }

    @Test
    void testFilterByLabel() throws Exception {
        Task task1 = new Task();
        task1.setTitle(TASK_1);
        task1.setContent(TEST_CONTENT);
        task1.setTaskStatus(testStatus);
        task1.setAssignee(testUser);
        task1.getLabels().add(testLabel);
        taskRepository.save(task1);

        Label anotherLabel = new Label();
        anotherLabel.setName("Another Label");
        labelRepository.save(anotherLabel);

        Task task2 = new Task();
        task2.setTitle(TASK_2);
        task2.setContent(TEST_CONTENT);
        task2.setTaskStatus(testStatus);
        task2.setAssignee(testUser);
        task2.getLabels().add(anotherLabel);
        taskRepository.save(task2);

        mockMvc.perform(get("/api/tasks?labelId=" + testLabel.getId())
                .header(AUTH, BEARER + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath(TITLE).value(TASK_1))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").value(org.hamcrest.Matchers.hasSize(1)));
    }
}
