package com.digiworld.vakeelpro.dto;

import lombok.Data;

import java.util.Map;
import java.util.Set;

@Data
public class LoginResponse {
    private String token;
    private String username;
    private String accountType;
    private Set<String> roles;
    private Map<String, Set<String>> featurePrivileges;
    private OrganizationDTO organization; // Optional

    @Data
    public static class OrganizationDTO {
        private String name;
        private String address;
        private String lawFirmData;
    }
}