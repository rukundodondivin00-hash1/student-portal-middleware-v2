package com.auca.studentportal.dto;

import lombok.Data;

@Data
public class UsernamePasswordRequest {
    private String username;
    private String password;
}