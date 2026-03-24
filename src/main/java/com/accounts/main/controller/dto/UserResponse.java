package com.accounts.main.controller.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class UserResponse {
    private Long userId;
    private String firstName;
    private String lastName;
    private String username;
    private String email;
    private boolean enabled;
    private boolean confirmed;
    private LocalDateTime createdAt;
}
