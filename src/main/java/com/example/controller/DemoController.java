package com.example.controller;

import com.example.dto.UserDTO;
import com.example.mapper.UserMapper;
import com.example.model.User;
import com.example.service.DemoService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/demo")
public class DemoController {

    private final DemoService demoService;
    private final UserMapper userMapper;

    public DemoController(DemoService demoService, UserMapper userMapper) {
        this.demoService = demoService;
        this.userMapper = userMapper;
    }

    @PostMapping("/error-no-transaction")
    public String demoErrorNoTransaction(@RequestBody UserDTO userDTO) {
        try {
            User user = userMapper.toEntity(userDTO);
            demoService.failedTransactionDemo(user);
            return "Успех";
        } catch (Exception e) {
            return "ОШИБКА (Без транзакции): " + e.getMessage()
                + ". Проверьте pgAdmin — пользователь сохранился, хотя была ошибка!";
        }
    }

    @PostMapping("/success-transaction")
    public String demoSuccessTransaction(@RequestBody UserDTO userDTO) {
        try {
            User user = userMapper.toEntity(userDTO);
            demoService.successTransactionDemo(user);
            return "УСПЕХ: И пользователь, и парковка в базе!";
        } catch (Exception e) {
            return "ОШИБКА: " + e.getMessage();
        }
    }

    @GetMapping("/n-plus-one")
    public String demonstrateNPlusOne() {
        demoService.demonstrateNPlusOneProblem();
        return "Проблема N+1 выведена в консоль";
    }

    @GetMapping("/solution")
    public String demonstrateSolution() {
        demoService.demonstrateSolutionWithJoinFetch();
        return "Решение N+1 выведено в консоль";
    }
}