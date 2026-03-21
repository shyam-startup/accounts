package com.accounts.main.controller.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthorizeResponse {

    private String redirectUrl;
}
