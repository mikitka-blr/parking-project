package com.example.controller;

import com.example.dto.UserDTO;
import com.example.model.User;
import com.example.service.DemoService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/demo")
public class DemoController {

    private final DemoService demoService;

    public DemoController(DemoService demoService) {
        this.demoService = demoService;
    }

    private User convertToEntity(UserDTO userDTO) {
        User user = new User();
        user.setFullName(userDTO.getFullName());
        user.setEmail(userDTO.getEmail());
        user.setPhone(userDTO.getPhone());
        user.setActive(userDTO.isActive());
        return user;
    }

    @PostMapping("/error-no-transaction")
    public String demoErrorNoTransaction(@RequestBody UserDTO userDTO) {
        try {
            User user = convertToEntity(userDTO);
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
            User user = convertToEntity(userDTO);
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