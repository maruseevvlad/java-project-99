package hexlet.code.service;

import hexlet.code.dto.UserCreateDTO;
import hexlet.code.dto.UserDTO;
import hexlet.code.dto.UserUpdateDTO;
import hexlet.code.model.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    List<UserDTO> getAll();
    UserDTO create(UserCreateDTO userCreateDTO);
    UserDTO findById(Long id);
    User findByIdEntity(Long id);
    UserDTO update(Long id, UserUpdateDTO userUpdateDTO);
    void delete(Long id);
    Optional<User> findByEmail(String email);
    String findEmailById(Long id);
}
