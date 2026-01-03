package com.digiworld.vakeelpro.service.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.digiworld.vakeelpro.constants.AccountType;
import com.digiworld.vakeelpro.entities.FeaturePrivilege;
import com.digiworld.vakeelpro.entities.Role;
import com.digiworld.vakeelpro.entities.User;
import com.digiworld.vakeelpro.repositories.FeaturePrivilegeRepository;
import com.digiworld.vakeelpro.repositories.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FeatureAssignmentService {

    private final FeaturePrivilegeRepository featurePrivilegeRepository;
    

    @Autowired
    private UserRepository userRepository;


    /**
     * Fetches feature privileges for a user and formats them as a JSON-like map.
     *
     * @param user The user for whom feature privileges are being retrieved.
     * @return Map of feature names to their allowed actions.
     */
    @Transactional
	public Map<String, Set<String>> getFeaturePrivilegesForUser(User user) {
        Map<String, Set<String>> featurePrivilegesMap = new HashMap<>();

        // Fetch privileges for each role
        for (Role role : user.getRoles()) {
            Set<FeaturePrivilege> rolePrivileges = featurePrivilegeRepository.findByRole_Name(role.getName());

            for (FeaturePrivilege privilege : rolePrivileges) {
                String featureName = privilege.getModuleName().name().toUpperCase();

                // Apply INDIVIDUAL restrictions
                if (user.getAccountType() == AccountType.INDIVIDUAL) {
                    Set<String> excludedFeatures = Set.of("COMPANYPROFILE", "ROLEMANAGEMENT"); // Fixed syntax error
                    if (excludedFeatures.contains(featureName)) {
                        continue; // Skip restricted features
                    }
                }

                // Add allowed actions to response map
                Set<String> actions = featurePrivilegesMap.computeIfAbsent(featureName, k -> new HashSet<>());

                if (privilege.isCanCreate()) actions.add("CREATE");
                if (privilege.isCanRead()) actions.add("READ");
                if (privilege.isCanUpdate()) actions.add("UPDATE");
                if (privilege.isCanDelete()) actions.add("DELETE");
            }
        }

        return featurePrivilegesMap;
    }

    @Transactional
    public Map<String, Set<String>> getUserFeatures(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Set<Role> roles = user.getRoles(); // Fetch user's roles
        Map<String, Set<String>> featureMap = new HashMap<>();

        // Iterate through roles and get associated feature privileges
        for (Role role : roles) {
            for (FeaturePrivilege privilege : role.getPrivileges()) {
                String moduleName = privilege.getModuleName().name().toUpperCase();

                Set<String> actions = featureMap.computeIfAbsent(moduleName, k -> new HashSet<>());

                if (privilege.isCanCreate()) actions.add("CREATE");
                if (privilege.isCanRead()) actions.add("READ");
                if (privilege.isCanUpdate()) actions.add("UPDATE");
                if (privilege.isCanDelete()) actions.add("DELETE");
            }
        }


        return featureMap;
    }

}
