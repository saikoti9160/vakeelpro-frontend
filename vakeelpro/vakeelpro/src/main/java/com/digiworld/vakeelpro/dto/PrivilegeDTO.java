package com.digiworld.vakeelpro.dto;

import com.digiworld.vakeelpro.constants.Modules;

import lombok.Data;

@Data
public class PrivilegeDTO {
    private Modules moduleName;
    private boolean canCreate;
    private boolean canRead;
    private boolean canUpdate;
    private boolean canDelete;
}
