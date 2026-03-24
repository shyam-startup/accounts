package com.accounts.main.controller.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AvailableAppResponse {

    private String clientId;
    private String clientName;
    private String redirectUri;
}
