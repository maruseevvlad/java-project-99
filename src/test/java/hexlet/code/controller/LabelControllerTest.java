package hexlet.code.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.AppApplication;
import hexlet.code.dto.LabelDTO;
import hexlet.code.model.Label;
import hexlet.code.model.User;
import hexlet.code.repository.LabelRepository;
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
public class LabelControllerTest {

    private static final String TEST_LABEL = "Test Label";
    private static final String AUTH = "Authorization";
    private static final String BEARER = "Bearer ";
    private static final String API_LABELS_ID = "/api/labels/{id}";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private LabelRepository labelRepository;

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
        labelRepository.deleteAll();
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
        Label label = new Label();
        label.setName(TEST_LABEL);
        labelRepository.save(label);

        MvcResult result = mockMvc.perform(get("/api/labels")
                .header(AUTH, BEARER + token))
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        List<LabelDTO> labelDTOs = objectMapper.readValue(content, new TypeReference<List<LabelDTO>>() { });

        List<Label> labelsFromDb = labelRepository.findAll();

        assertThat(labelDTOs).hasSize(labelsFromDb.size());

        List<String> labelNamesFromDb = labelsFromDb.stream()
                .map(Label::getName)
                .toList();

        List<String> labelNamesFromResponse = labelDTOs.stream()
                .map(LabelDTO::getName)
                .toList();

        assertThat(labelNamesFromResponse).containsExactlyElementsOf(labelNamesFromDb);
    }

    @Test
    void testShow() throws Exception {
        Label label = new Label();
        label.setName(TEST_LABEL);
        labelRepository.save(label);

        mockMvc.perform(get(API_LABELS_ID, label.getId())
                .header(AUTH, BEARER + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(TEST_LABEL));
    }

    @Test
    void testCreate() throws Exception {
        String labelData = "{\"name\":\"New Label\"}";
        mockMvc.perform(post("/api/labels")
                .contentType(MediaType.APPLICATION_JSON)
                .content(labelData)
                .header(AUTH, BEARER + token))
                .andExpect(status().isCreated());

        Label label = labelRepository.findByName("New Label")
                .orElseThrow(() -> new AssertionError("Label not found"));

        assertThat(label.getName()).isEqualTo("New Label");
    }

    @Test
    void testUpdate() throws Exception {
        Label label = new Label();
        label.setName("Old Label");
        labelRepository.save(label);

        String updateData = "{\"name\":\"Updated Label\"}";

        mockMvc.perform(put(API_LABELS_ID, label.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateData)
                .header(AUTH, BEARER + token))
                .andExpect(status().isOk());

        Label updatedLabel = labelRepository.findById(label.getId()).orElse(null);
        assertThat(updatedLabel).isNotNull();
        assertThat(updatedLabel.getName()).isEqualTo("Updated Label");
    }

    @Test
    void testDestroy() throws Exception {
        Label label = new Label();
        label.setName(TEST_LABEL);
        labelRepository.save(label);

        mockMvc.perform(delete(API_LABELS_ID, label.getId())
                .header(AUTH, BEARER + token))
                .andExpect(status().isNoContent());

        assertThat(labelRepository.existsById(label.getId())).isFalse();
    }
}
