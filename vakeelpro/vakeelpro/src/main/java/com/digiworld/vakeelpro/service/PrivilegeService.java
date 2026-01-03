package com.digiworld.vakeelpro.service;

import java.util.List;

import com.digiworld.vakeelpro.entities.Privilege;

public interface PrivilegeService {

    Privilege createPrivilege(String privilegeName);

    void deletePrivilege(Long privilegeId);

    Privilege getPrivilegeById(Long privilegeId);

    Privilege getPrivilegeByName(String privilegeName);

    List<Privilege> getAllPrivileges();
}