package com.example.dto;

import com.example.model.BaseUser;

public class UserDTO extends BaseUser {

    public UserDTO() {
    }

    public UserDTO(Long id, String fullName, String email, String phone, boolean active) {
        setId(id);
        setFullName(fullName);
        setEmail(email);
        setPhone(phone);
        setActive(active);
    }
}