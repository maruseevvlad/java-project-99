package hexlet.code.controller;

import hexlet.code.dto.LabelCreateDto;
import hexlet.code.dto.LabelDto;
import hexlet.code.dto.LabelUpdateDto;
import hexlet.code.model.Label;
import hexlet.code.service.LabelService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/labels")
public class LabelController {
    private final LabelService labelService;

    public LabelController(LabelService labelService) {
        this.labelService = labelService;
    }

    @GetMapping
    public List<LabelDto> index() {
        return labelService.findAll().stream()
                .map(LabelDto::fromEntity)
                .toList();
    }

    @GetMapping("/{id}")
    public LabelDto show(@PathVariable Long id) {
        Label label = labelService.findById(id);
        return LabelDto.fromEntity(label);
    }

    @PostMapping
    public ResponseEntity<LabelDto> create(@Valid @RequestBody LabelCreateDto data) {
        Label label = labelService.create(data);
        return ResponseEntity.status(HttpStatus.CREATED).body(LabelDto.fromEntity(label));
    }

    @PutMapping("/{id}")
    public LabelDto update(@PathVariable Long id, @Valid @RequestBody LabelUpdateDto data) {
        Label label = labelService.update(id, data);
        return LabelDto.fromEntity(label);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        labelService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
