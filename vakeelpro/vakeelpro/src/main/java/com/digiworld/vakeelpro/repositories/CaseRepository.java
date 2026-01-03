package com.digiworld.vakeelpro.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.digiworld.vakeelpro.entities.Case;
import com.digiworld.vakeelpro.entities.Organization;
import com.digiworld.vakeelpro.entities.User;

public interface CaseRepository extends JpaRepository<Case, Long> {
    List<Case> findByOrganization(Organization organization);
    List<Case> findByUser(User user);
}