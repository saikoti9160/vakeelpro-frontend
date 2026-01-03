package com.digiworld.vakeelpro.service;

import java.util.List;

import com.digiworld.vakeelpro.entities.Case;
import com.digiworld.vakeelpro.entities.User;

public interface CaseService {
    Case createCase(User creator, Case caseDetails);
    List<Case> getCasesForUser(User user);
}