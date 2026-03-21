package com.accounts.main.controller.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserResponse {

    private Long userId;
    private String username;
    private String email;
}
