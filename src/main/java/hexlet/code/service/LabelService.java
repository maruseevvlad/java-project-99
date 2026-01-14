package hexlet.code.service;

import hexlet.code.dto.LabelCreateDTO;
import hexlet.code.dto.LabelDTO;
import hexlet.code.dto.LabelUpdateDTO;
import hexlet.code.model.Label;

import java.util.List;

public interface LabelService {
    List<LabelDTO> getAll();
    LabelDTO create(LabelCreateDTO labelCreateDTO);
    LabelDTO findById(Long id);
    Label findByIdEntity(Long id);
    LabelDTO update(Long id, LabelUpdateDTO labelUpdateDTO);
    void delete(Long id);
    Label findByName(String name);
}
