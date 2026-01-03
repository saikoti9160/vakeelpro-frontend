package com.digiworld.vakeelpro.service;

import com.digiworld.vakeelpro.dto.LoginRequest;
import com.digiworld.vakeelpro.dto.LoginResponse;

public interface LoginService {
    LoginResponse login(LoginRequest loginRequest) throws Exception;
    void forgotPassword(String email) throws Exception;
    void updatePassword(String token, String newPassword) throws Exception;
    void resetPassword(String email) throws Exception;
    void updatePasswordForLoggedInUser(String username, String currentPassword, String newPassword) throws Exception; // New method
}