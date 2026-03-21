package com.accounts.main.controller.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserInfoResponse {

    private String userId;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
}
