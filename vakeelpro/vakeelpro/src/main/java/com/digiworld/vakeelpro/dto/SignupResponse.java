package com.digiworld.vakeelpro.dto;

import lombok.Data;

@Data
public class SignupResponse {
    private String username;
    private String email;
    private String accountType;
    private String message;
}