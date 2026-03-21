package com.accounts.main.controller.dto;

import lombok.Data;

@Data
public class SignupRequest {

    private String username;
    private String email;
    private String password;
    private String firstName;
    private String lastName;
}
