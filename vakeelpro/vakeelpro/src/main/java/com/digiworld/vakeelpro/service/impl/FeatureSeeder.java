package com.digiworld.vakeelpro.service.impl;

import java.util.HashSet;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.digiworld.vakeelpro.constants.Constants;
import com.digiworld.vakeelpro.constants.Modules;
import com.digiworld.vakeelpro.entities.FeaturePrivilege;
import com.digiworld.vakeelpro.entities.Role;
import com.digiworld.vakeelpro.repositories.FeaturePrivilegeRepository;
import com.digiworld.vakeelpro.repositories.RoleRepository;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class FeatureSeeder {

    private final FeaturePrivilegeRepository featurePrivilegeRepository;
    private final RoleRepository roleRepository;

    @PostConstruct
    @Transactional
    public void seedRolesAndPrivileges() {
        Role userRole = roleRepository.findByName(Constants.USER_ROLE);
        if (userRole == null) {
            userRole = new Role();
            userRole.setName(Constants.USER_ROLE);
        } else {
            userRole.getPrivileges().clear(); // Ensure idempotency
        }

        Role adminRole = roleRepository.findByName(Constants.ADMIN_ROLE);
        if (adminRole == null) {
            adminRole = new Role();
            adminRole.setName(Constants.ADMIN_ROLE);
        } else {
            adminRole.getPrivileges().clear();
        }

        // Set fresh privilege sets
        userRole.setPrivileges(new HashSet<>());
        adminRole.setPrivileges(new HashSet<>());

        for (Modules module : Modules.values()) {
            // USER Role: Limited access
        	 if (module != Modules.RoleManagement && module != Modules.UserManagement) {
                FeaturePrivilege userPrivilege = new FeaturePrivilege();
                userPrivilege.setModuleName(module);
                userPrivilege.setCanRead(false);
                userPrivilege.setCanCreate(false);
                userPrivilege.setCanUpdate(false);
                userPrivilege.setCanDelete(false);
                userPrivilege.setRole(userRole);
                userRole.getPrivileges().add(userPrivilege);
            }

            // ADMIN Role: Full access
            FeaturePrivilege adminPrivilege = new FeaturePrivilege();
            adminPrivilege.setModuleName(module);
            adminPrivilege.setCanCreate(true);
            adminPrivilege.setCanRead(true);
            adminPrivilege.setCanUpdate(true);
            adminPrivilege.setCanDelete(true);
            adminPrivilege.setRole(adminRole);
            adminRole.getPrivileges().add(adminPrivilege);
        }

        // Save roles (with cascading privileges)
        roleRepository.save(userRole);
        roleRepository.save(adminRole);

        System.out.println("✅ Roles and feature privileges seeded successfully!");
    }
}