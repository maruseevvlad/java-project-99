package hexlet.code.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.model.TaskStatus;
import hexlet.code.repository.TaskStatusRepository;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class TaskStatusControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Test
    void shouldListTaskStatusesWithoutAuth() throws Exception {
        mockMvc.perform(get("/api/task_statuses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(5))
                .andExpect(jsonPath("$[*].slug", hasItem("draft")));
    }

    @Test
    void shouldShowTaskStatusById() throws Exception {
        TaskStatus status = taskStatusRepository.findBySlug("draft").orElseThrow();

        mockMvc.perform(get("/api/task_statuses/" + status.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.slug").value("draft"))
                .andExpect(jsonPath("$.createdAt").isNotEmpty());
    }

    @Test
    void shouldRequireAuthForCreatingStatus() throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("name", "New");
        params.put("slug", "new");

        mockMvc.perform(post("/api/task_statuses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(params)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldCreateTaskStatus() throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("name", "In Progress");
        params.put("slug", "in_progress");

        mockMvc.perform(post("/api/task_statuses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(params))
                        .header(HttpHeaders.AUTHORIZATION, bearer(loginAsAdmin())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("In Progress"))
                .andExpect(jsonPath("$.slug").value("in_progress"));

        assertThat(taskStatusRepository.findBySlug("in_progress")).isPresent();
    }

    @Test
    void shouldUpdateTaskStatus() throws Exception {
        TaskStatus status = taskStatusRepository.findBySlug("draft").orElseThrow();

        Map<String, Object> params = new HashMap<>();
        params.put("name", "Draft Updated");

        mockMvc.perform(put("/api/task_statuses/" + status.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(params))
                        .header(HttpHeaders.AUTHORIZATION, bearer(loginAsAdmin())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Draft Updated"))
                .andExpect(jsonPath("$.slug").value("draft"));

        TaskStatus updated = taskStatusRepository.findBySlug("draft").orElseThrow();
        assertThat(updated.getName()).isEqualTo("Draft Updated");
    }

    @Test
    void shouldDeleteTaskStatus() throws Exception {
        TaskStatus status = taskStatusRepository.findBySlug("draft").orElseThrow();

        mockMvc.perform(delete("/api/task_statuses/" + status.getId())
                        .header(HttpHeaders.AUTHORIZATION, bearer(loginAsAdmin())))
                .andExpect(status().isNoContent());

        List<TaskStatus> remaining = taskStatusRepository.findAll();
        assertThat(remaining).noneMatch(s -> s.getId().equals(status.getId()));
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
