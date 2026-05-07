package com.example.controller;

import com.example.dto.UserDTO;
import com.example.mapper.UserMapper;
import com.example.model.User;
import com.example.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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
import java.util.List;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@Tag(name = "Пользователи", description = "CRUD операции для управления пользователями")
public class UserController {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(UserController.class);

    private final UserService userService;
    private final UserMapper userMapper;

    public UserController(UserService userService, UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    // Debug endpoint to delete user with detailed response (dev only)
    @PostMapping("/debug/delete/{id}")
    public ResponseEntity<Map<String, Object>> debugDeleteUser(@PathVariable Long id) {
        LOG.info("Debug delete request for user id={}", id);
        Map<String, Object> resp = new HashMap<>();
        try {
            boolean deleted = userService.deleteUser(id);
            resp.put("deleted", deleted);
            resp.put("id", id);
            if (deleted) {
                LOG.info("Debug: user id={} deleted", id);
                return new ResponseEntity<>(resp, HttpStatus.OK);
            } else {
                LOG.info("Debug: user id={} not found", id);
                resp.put("message", "User not found");
                return new ResponseEntity<>(resp, HttpStatus.NOT_FOUND);
            }
        } catch (Exception ex) {
            LOG.error("Debug delete failed for user id={}", id, ex);
            resp.put("deleted", false);
            resp.put("error", ex.getMessage());
            return new ResponseEntity<>(resp, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping
    @Operation(summary = "Создать пользователя")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Пользователь создан"),
        @ApiResponse(responseCode = "400", description = "Неверные данные запроса")
    })
    public ResponseEntity<UserDTO> createUser(@Valid @RequestBody UserDTO userDTO) {
        User user = userMapper.toEntity(userDTO);
        // set password explicitly from DTO (mapper does not copy password to avoid exposing it)
        user.setPassword(userDTO.getPassword());
        User createdUser = userService.createUser(user);
        return new ResponseEntity<>(userMapper.toDTO(createdUser), HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Получить всех пользователей")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        if (users.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        List<UserDTO> userDTOs = users.stream()
            .map(userMapper::toDTO)
            .toList();
        return new ResponseEntity<>(userDTOs, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить пользователя по ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Пользователь найден"),
        @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    })
    public ResponseEntity<UserDTO> getUserById(
        @Parameter(description = "ID пользователя", example = "1") @PathVariable Long id) {
        User user = userService.getUserById(id);
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(userMapper.toDTO(user), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновить пользователя")
    public ResponseEntity<UserDTO> updateUser(
        @Parameter(description = "ID пользователя", example = "1") @PathVariable Long id,
        @Valid @RequestBody UserDTO userDTO) {
        User user = userMapper.toEntity(userDTO);
        User updatedUser = userService.updateUser(id, user);
        if (updatedUser == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(userMapper.toDTO(updatedUser), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить пользователя")
    public ResponseEntity<Void> deleteUser(
        @Parameter(description = "ID пользователя", example = "1") @PathVariable Long id) {
        LOG.info("Request to delete user id={}", id);
        boolean deleted = false;
        try {
            deleted = userService.deleteUser(id);
        } catch (Exception ex) {
            LOG.error("Error while deleting user id={}", id, ex);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if (!deleted) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        LOG.info("User id={} deleted", id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}