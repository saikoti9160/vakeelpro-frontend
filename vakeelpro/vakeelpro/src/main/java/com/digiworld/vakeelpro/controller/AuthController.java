package com.digiworld.vakeelpro.controller;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.digiworld.vakeelpro.dto.LoginRequest;
import com.digiworld.vakeelpro.dto.LoginResponse;
import com.digiworld.vakeelpro.dto.SignupRequest;
import com.digiworld.vakeelpro.dto.SignupResponse;
import com.digiworld.vakeelpro.entities.User;
import com.digiworld.vakeelpro.entities.VerificationToken;
import com.digiworld.vakeelpro.repositories.UserRepository;
import com.digiworld.vakeelpro.repositories.VerificationTokenRepository;
import com.digiworld.vakeelpro.service.LoginService;
import com.digiworld.vakeelpro.service.UserService;

@RestController
@CrossOrigin
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private LoginService loginService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private VerificationTokenRepository verificationTokenRepository;
    
    @Autowired
    private UserRepository userRepository;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) throws Exception {
        LoginResponse response = loginService.login(loginRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestParam String email) throws Exception {
        loginService.forgotPassword(email);
        return ResponseEntity.ok("Password reset link sent to your email");
    }

    @PostMapping("/update-password")
    public ResponseEntity<String> updatePassword(@RequestParam String token, @RequestParam String newPassword) throws Exception {
        loginService.updatePassword(token, newPassword);
        return ResponseEntity.ok("Password updated successfully");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestParam String email) throws Exception {
        loginService.resetPassword(email);
        return ResponseEntity.ok("Password reset link sent to your email");
    }
    
    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(
            @RequestParam String currentPassword,
            @RequestParam String newPassword,
            Authentication authentication) throws Exception {
        String username = authentication.getName();
        loginService.updatePasswordForLoggedInUser(username, currentPassword, newPassword);
        return ResponseEntity.ok("Password changed successfully");
    }

    @PostMapping("/signup")
    public ResponseEntity<SignupResponse> signup(@RequestBody SignupRequest signupRequest) {
        SignupResponse response = userService.signup(signupRequest);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/verify")
    public ResponseEntity<String> verifyAccount(@RequestParam String token) {
        userService.verifyAccount(token);
        return ResponseEntity.ok("Account verified successfully");
    }
    
    @PostMapping("/resend-verification")
    public ResponseEntity<String> resendVerification(@RequestParam String email) throws Exception {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        if (user.isVerified()) {
            throw new RuntimeException("Account is already verified");
        }

        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(token);
        verificationToken.setUser(user);
        verificationTokenRepository.save(verificationToken);

        String verificationLink = "http://localhost:8085/api/auth/verify?token=" + token;
        userService.sendVerificationEmail(user.getEmail(), verificationLink);
        return ResponseEntity.ok("Verification email resent");
    }
}