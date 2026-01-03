package com.digiworld.vakeelpro.service.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.digiworld.vakeelpro.constants.Modules;
import com.digiworld.vakeelpro.entities.FeaturePrivilege;
import com.digiworld.vakeelpro.entities.Role;
import com.digiworld.vakeelpro.repositories.FeaturePrivilegeRepository;
import com.digiworld.vakeelpro.repositories.RoleRepository;
import com.digiworld.vakeelpro.service.RoleService;

@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private FeaturePrivilegeRepository featurePrivilegeRepository;

    @Override
    @Transactional
    public Role createRole(String roleName, Set<FeaturePrivilege> privilegeData) {
        Role role = new Role();
        role.setName(roleName);

        // Set<Role> → persistence relation
        Set<FeaturePrivilege> privileges = privilegeData.stream()
            .peek(privilege -> privilege.setRole(role)) // Assign back-reference
            .collect(Collectors.toSet());

        role.setPrivileges(privileges);
        return roleRepository.save(role);
    }

    @Override
    @Transactional
  
    public Role updateRole(Long roleId, String roleName, Set<FeaturePrivilege> privilegeData) {
        Role existingRole = roleRepository.findById(roleId)
            .orElseThrow(() -> new RuntimeException("Role not found with id: " + roleId));

        existingRole.setName(roleName);

        if (privilegeData != null && !privilegeData.isEmpty()) {
            // Clear old privileges
            existingRole.getPrivileges().clear();

            // Set role reference on each privilege and add to role
            Set<FeaturePrivilege> updatedPrivileges = privilegeData.stream()
                .peek(privilege -> privilege.setRole(existingRole))
                .collect(Collectors.toSet());

            existingRole.getPrivileges().addAll(updatedPrivileges);
        }

        return roleRepository.save(existingRole);
    }



    @Override
    @Transactional
    public void deleteRole(Long roleId) {
        Role role = roleRepository.findById(roleId)
            .orElseThrow(() -> new RuntimeException("Role not found with id: " + roleId));
        roleRepository.delete(role);
    }

    @Override
    public Role getRoleById(Long roleId) {
        return roleRepository.findById(roleId)
            .orElseThrow(() -> new RuntimeException("Role not found with id: " + roleId));
    }

    @Override
    public Role getRoleByName(String roleName) {
        Role role = roleRepository.findByName(roleName);
        if (role == null) {
            throw new RuntimeException("Role not found with name: " + roleName);
        }
        return role;
    }

    @Override
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }
    

    

}