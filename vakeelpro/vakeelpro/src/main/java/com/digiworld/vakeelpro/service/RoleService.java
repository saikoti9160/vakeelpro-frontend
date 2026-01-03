package com.digiworld.vakeelpro.service;

import java.util.List;
import java.util.Set;

import com.digiworld.vakeelpro.entities.FeaturePrivilege;
import com.digiworld.vakeelpro.entities.Role;

public interface RoleService {

	public Role createRole(String roleName, Set<FeaturePrivilege> privilegeData);

    Role updateRole(Long Id, String roleName, Set<FeaturePrivilege> privilegeData);

    void deleteRole(Long roleId);

    Role getRoleById(Long roleId);

    Role getRoleByName(String roleName);

    List<Role> getAllRoles();

    
}