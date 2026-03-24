package com.accounts.main.controller.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ConnectedAppResponse {

    private String clientId;
    private String clientName;
    private String redirectUri;
    private LocalDateTime consentedAt;
}
