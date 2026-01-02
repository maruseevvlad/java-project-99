package hexlet.code.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class LabelControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String login() throws Exception {
        String credentials = objectMapper.writeValueAsString(Map.of(
                "username", "hexlet@example.com",
                "password", "qwerty"
        ));
        MvcResult result = mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(credentials))
                .andExpect(status().isOk())
                .andReturn();
        return result.getResponse().getContentAsString();
    }

    @Test
    void labelsRequireAuthentication() throws Exception {
        mockMvc.perform(get("/api/labels"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void createAndFetchLabel() throws Exception {
        String token = login();
        String labelBody = objectMapper.writeValueAsString(Map.of("name", "priority"));

        MvcResult createResult = mockMvc.perform(post("/api/labels")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(labelBody))
                .andExpect(status().isCreated())
                .andReturn();

        Map<?, ?> createdLabel = objectMapper.readValue(createResult.getResponse().getContentAsString(), Map.class);
        assertThat(createdLabel.get("name")).isEqualTo("priority");

        mockMvc.perform(get("/api/labels")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void updateLabelName() throws Exception {
        String token = login();
        String createBody = objectMapper.writeValueAsString(Map.of("name", "backend"));
        MvcResult createResult = mockMvc.perform(post("/api/labels")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createBody))
                .andExpect(status().isCreated())
                .andReturn();
        Map<?, ?> createdLabel = objectMapper.readValue(createResult.getResponse().getContentAsString(), Map.class);

        String updateBody = objectMapper.writeValueAsString(Map.of("name", "frontend"));
        MvcResult updateResult = mockMvc.perform(put("/api/labels/" + createdLabel.get("id"))
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateBody))
                .andExpect(status().isOk())
                .andReturn();
        Map<?, ?> updatedLabel = objectMapper.readValue(updateResult.getResponse().getContentAsString(), Map.class);

        assertThat(updatedLabel.get("name")).isEqualTo("frontend");
    }

    @Test
    void preventDeletingLinkedLabel() throws Exception {
        String token = login();
        String labelBody = objectMapper.writeValueAsString(Map.of("name", "api"));
        MvcResult labelResult = mockMvc.perform(post("/api/labels")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(labelBody))
                .andExpect(status().isCreated())
                .andReturn();
        Map<?, ?> label = objectMapper.readValue(labelResult.getResponse().getContentAsString(), Map.class);

        String taskBody = objectMapper.writeValueAsString(Map.of(
                "title", "Implement endpoint",
                "status", "draft",
                "label_ids", List.of(label.get("id"))
        ));
        mockMvc.perform(post("/api/tasks")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(taskBody))
                .andExpect(status().isCreated());

        mockMvc.perform(delete("/api/labels/" + label.get("id"))
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isBadRequest());
    }
}
