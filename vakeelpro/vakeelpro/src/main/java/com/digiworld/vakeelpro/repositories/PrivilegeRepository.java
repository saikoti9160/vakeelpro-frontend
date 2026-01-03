package com.digiworld.vakeelpro.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.digiworld.vakeelpro.entities.Privilege;

public interface PrivilegeRepository extends JpaRepository<Privilege, Long> {
    Privilege findByName(String name);
}