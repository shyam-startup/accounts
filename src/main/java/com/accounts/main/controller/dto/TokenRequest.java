package com.accounts.main.controller.dto;

import lombok.Data;

@Data
public class TokenRequest {

    private String clientId;
    private String clientSecret;
    private String code;
}
