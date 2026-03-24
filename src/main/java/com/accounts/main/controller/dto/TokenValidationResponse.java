package com.accounts.main.controller.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class TokenValidationResponse {

    private boolean valid;
    private String reason;
    private UserInfoResponse user;
    private LocalDateTime expiresAt;
}
