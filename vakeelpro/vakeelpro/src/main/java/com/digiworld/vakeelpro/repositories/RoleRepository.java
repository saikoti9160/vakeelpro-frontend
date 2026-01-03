package com.digiworld.vakeelpro.repositories;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.digiworld.vakeelpro.entities.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {
	@EntityGraph(attributePaths = { "privileges" })
	Role findByName(String name);

}