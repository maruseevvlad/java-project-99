package hexlet.code.service;

import hexlet.code.dto.LabelCreateDto;
import hexlet.code.dto.LabelUpdateDto;
import hexlet.code.model.Label;
import hexlet.code.repository.LabelRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@Service
public class LabelService {
    private final LabelRepository labelRepository;

    public LabelService(LabelRepository labelRepository) {
        this.labelRepository = labelRepository;
    }

    public Label create(LabelCreateDto data) {
        Label label = new Label();
        label.setName(data.name());
        return labelRepository.save(label);
    }

    public List<Label> findAll() {
        return labelRepository.findAll();
    }

    public Label findById(Long id) {
        return labelRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Label not found"));
    }

    public Label update(Long id, LabelUpdateDto data) {
        Label label = findById(id);
        if (data.name() != null) {
            label.setName(data.name());
        }
        return labelRepository.save(label);
    }

    public void delete(Long id) {
        Label label = findById(id);
        if (labelRepository.existsByIdAndTasksIsNotEmpty(label.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot delete label linked to tasks");
        }
        labelRepository.delete(label);
    }
}
