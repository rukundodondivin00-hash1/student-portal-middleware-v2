package com.auca.studentportal.dto;

import lombok.Data;
import java.util.List;

@Data
public class SignInResponse {
    private String username;
    private String email;
    private String role;
    private List<String> permissions;
    private String fullName;
}
