package com.digiworld.vakeelpro.controller;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.digiworld.vakeelpro.constants.AccountType;
import com.digiworld.vakeelpro.dto.RoleDTO;
import com.digiworld.vakeelpro.entities.Case;
import com.digiworld.vakeelpro.entities.FeaturePrivilege;
import com.digiworld.vakeelpro.entities.Role;
import com.digiworld.vakeelpro.entities.User;
import com.digiworld.vakeelpro.service.CaseService;
import com.digiworld.vakeelpro.service.RoleService;
import com.digiworld.vakeelpro.service.UserService;

@RestController
@RequestMapping("/api/staff")
public class StaffController {

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private CaseService caseService;

    @PostMapping("/create")
    public ResponseEntity<User> createStaff(@RequestParam Long adminId, @RequestBody User staff, @RequestParam Set<String> roleNames) {
        User createdStaff = userService.createStaff(adminId, staff, roleNames);
        return ResponseEntity.ok(createdStaff);
    }

    @PostMapping("/make-admin")
    public ResponseEntity<String> makeStaffAdmin(@RequestParam Long adminId, @RequestParam Long staffId) {
        userService.makeStaffAdmin(adminId, staffId);
        return ResponseEntity.ok("Staff promoted to admin");
    }

    @PostMapping("/role/create")
    public ResponseEntity<Role> createRole(@RequestParam Long userId, @RequestBody RoleDTO roleDTO) {
    	  User user = userService.getUserById(userId);
    	    if (!user.getAccountType().equals(AccountType.ORGANIZATION) &&
    	        !user.getAccountType().equals(AccountType.SUPER_ADMIN)) {
    	        throw new RuntimeException("Only ORGANIZATION or SUPER_ADMIN can create roles");
    	    }

    	    Set<FeaturePrivilege> privileges = roleDTO.getPrivileges().stream()
    	        .map(dto -> {
    	            FeaturePrivilege fp = new FeaturePrivilege();
    	            fp.setModuleName(dto.getModuleName());
    	            fp.setCanCreate(dto.isCanCreate());
    	            fp.setCanRead(dto.isCanRead());
    	            fp.setCanUpdate(dto.isCanUpdate());
    	            fp.setCanDelete(dto.isCanDelete());
    	            return fp;
    	        })
    	        .collect(Collectors.toSet());

    	    Role createdRole = roleService.createRole(roleDTO.getRoleName(), privileges);
    	    return ResponseEntity.ok(createdRole);
    }

    @PostMapping("/case/create")
    public ResponseEntity<Case> createCase(@RequestParam Long userId, @RequestBody Case caseDetails) {
        User user = userService.getUserById(userId);
        Case createdCase = caseService.createCase(user, caseDetails);
        return ResponseEntity.ok(createdCase);
    }

    @GetMapping("/cases")
    public ResponseEntity<List<Case>> getCases(@RequestParam Long userId) {
        User user = userService.getUserById(userId);
        List<Case> cases = caseService.getCasesForUser(user);
        return ResponseEntity.ok(cases);
    }
}