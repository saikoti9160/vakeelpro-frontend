package com.digiworld.vakeelpro.dto;

import lombok.Data;

@Data
public class SignupRequest {
    private String username;
    private String password;
    private String email;
    private String accountType; // "INDIVIDUAL" or "ORGANIZATION"
    private OrganizationDTO organization; // Optional, required only for ORGANIZATION

    @Data
    public static class OrganizationDTO {
        private String name;
        private String address;
        private String lawFirmData; // Could be JSON or plain text
    }
}