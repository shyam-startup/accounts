package com.accounts.main.controller.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class AuthResponse {

    private String sessionToken;
    private String refreshToken;
    private LocalDateTime expiresAt;
    private Long userId;
    private String username;
    private String email;
}
