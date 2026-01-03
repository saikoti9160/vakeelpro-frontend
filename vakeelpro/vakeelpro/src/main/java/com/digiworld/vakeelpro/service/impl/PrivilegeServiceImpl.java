package com.digiworld.vakeelpro.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.digiworld.vakeelpro.entities.Privilege;
import com.digiworld.vakeelpro.repositories.PrivilegeRepository;
import com.digiworld.vakeelpro.service.PrivilegeService;

@Service
public class PrivilegeServiceImpl implements PrivilegeService {

    @Autowired
    private PrivilegeRepository privilegeRepository;

    @Override
    @Transactional
    public Privilege createPrivilege(String privilegeName) {
        Privilege privilege = new Privilege();
        privilege.setName(privilegeName);
        return privilegeRepository.save(privilege);
    }

    @Override
    @Transactional
    public void deletePrivilege(Long privilegeId) {
        Privilege privilege = privilegeRepository.findById(privilegeId)
            .orElseThrow(() -> new RuntimeException("Privilege not found with id: " + privilegeId));
        privilegeRepository.delete(privilege);
    }

    @Override
    public Privilege getPrivilegeById(Long privilegeId) {
        return privilegeRepository.findById(privilegeId)
            .orElseThrow(() -> new RuntimeException("Privilege not found with id: " + privilegeId));
    }

    @Override
    public Privilege getPrivilegeByName(String privilegeName) {
        Privilege privilege = privilegeRepository.findByName(privilegeName);
        if (privilege == null) {
            throw new RuntimeException("Privilege not found with name: " + privilegeName);
        }
        return privilege;
    }

    @Override
    public List<Privilege> getAllPrivileges() {
        return privilegeRepository.findAll();
    }
}