package com.digiworld.vakeelpro.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.digiworld.vakeelpro.entities.Organization;

public interface OrganizationRepository extends JpaRepository<Organization, Long> {
    Organization findByName(String name);
}