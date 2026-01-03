package com.digiworld.vakeelpro.service;

import java.util.List;
import java.util.Set;

import com.digiworld.vakeelpro.dto.SignupRequest;
import com.digiworld.vakeelpro.dto.SignupResponse;
import com.digiworld.vakeelpro.entities.User;

public interface UserService {

    User createUser(User user, Set<String> roleNames);

    User updateUser(Long userId, User userDetails, Set<String> roleNames);

    void deleteUser(Long userId);

    User getUserById(Long userId);

    User getUserByUsername(String username);

    List<User> getAllUsers();

    void assignRolesToUser(Long userId, Set<String> roleNames);
    
    User createStaff(Long adminUserId, User staff, Set<String> roleNames); // New method
    void makeStaffAdmin(Long adminUserId, Long staffId); // New method
    
    SignupResponse signup(SignupRequest signupRequest); // Updated to handle verification
    void verifyAccount(String token); // New method

	void sendVerificationEmail(String email, String verificationLink) throws Exception;
}