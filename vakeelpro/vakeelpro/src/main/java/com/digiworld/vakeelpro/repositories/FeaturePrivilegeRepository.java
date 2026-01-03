package com.digiworld.vakeelpro.repositories;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;

import com.digiworld.vakeelpro.entities.FeaturePrivilege;

public interface FeaturePrivilegeRepository extends JpaRepository<FeaturePrivilege, Long> {

	Set<FeaturePrivilege> findByRole_Name(String roleName);

}