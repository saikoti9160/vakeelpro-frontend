package com.digiworld.vakeelpro.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.digiworld.vakeelpro.constants.AccountType;
import com.digiworld.vakeelpro.entities.Case;
import com.digiworld.vakeelpro.entities.User;
import com.digiworld.vakeelpro.repositories.CaseRepository;
import com.digiworld.vakeelpro.service.CaseService;

@Service
public class CaseServiceImpl implements CaseService {

    @Autowired
    private CaseRepository caseRepository;

    @Override
    @Transactional
    public Case createCase(User creator, Case caseDetails) {
        if (creator.getAccountType().equals(AccountType.ORGANIZATION) || 
            creator.getAccountType().equals(AccountType.SUPER_ADMIN)) {
            caseDetails.setOrganization(creator.getOrganization());
        } else {
            caseDetails.setUser(creator); // Individual user case
        }
        return caseRepository.save(caseDetails);
    }

    @Override
    public List<Case> getCasesForUser(User user) {
        if (user.getAccountType().equals(AccountType.ORGANIZATION) || 
            (user.getAccountType().equals(AccountType.SUPER_ADMIN) && user.getOrganization() != null)) {
            return caseRepository.findByOrganization(user.getOrganization());
        }
        return caseRepository.findByUser(user); // Individual user cases
    }
}