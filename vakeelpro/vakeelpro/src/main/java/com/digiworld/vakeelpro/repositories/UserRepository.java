package com.digiworld.vakeelpro.repositories;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.digiworld.vakeelpro.entities.User;

public interface UserRepository extends JpaRepository<User, Long> {
	@EntityGraph(attributePaths = { "roles", "roles.privileges" })
    User findByUsername(String username);

	@EntityGraph(attributePaths = { "roles", "roles.privileges" })
	User findByEmail(String email);
}