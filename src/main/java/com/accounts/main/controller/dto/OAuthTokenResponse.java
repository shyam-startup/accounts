package com.accounts.main.controller.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class OAuthTokenResponse {

    private String accessToken;
    private String refreshToken;
    private LocalDateTime expiresAt;
    private UserInfoResponse user;
}
