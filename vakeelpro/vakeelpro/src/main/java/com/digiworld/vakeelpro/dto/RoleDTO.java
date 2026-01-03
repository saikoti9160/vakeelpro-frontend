package com.digiworld.vakeelpro.dto;

import java.util.Set;

import lombok.Data;

@Data
public class RoleDTO {
    private String roleName;
    private Set<PrivilegeDTO> privileges;
}
