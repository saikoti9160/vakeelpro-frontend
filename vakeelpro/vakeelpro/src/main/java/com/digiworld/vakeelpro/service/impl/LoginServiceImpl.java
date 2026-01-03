package com.digiworld.vakeelpro.service.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.digiworld.vakeelpro.dto.LoginRequest;
import com.digiworld.vakeelpro.dto.LoginResponse;
import com.digiworld.vakeelpro.entities.ResetToken;
import com.digiworld.vakeelpro.entities.Role;
import com.digiworld.vakeelpro.entities.User;
import com.digiworld.vakeelpro.repositories.ResetTokenRepository;
import com.digiworld.vakeelpro.repositories.UserRepository;
import com.digiworld.vakeelpro.service.LoginService;
import com.digiworld.vakeelpro.utils.JwtUtil;

import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class LoginServiceImpl implements LoginService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsService userDetailsService;

    
    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JavaMailSender mailSender;
    
    @Autowired
    private ResetTokenRepository resetTokenRepository;

    private final Map<String, String> tokenStore = new HashMap<>(); // Temporary token storage (replace with Redis/DB in production)
    public LoginResponse login(LoginRequest loginRequest) throws Exception {
        log.info("Login request received for: {}", loginRequest.getUsername());

        User user = userRepository.findByUsername(loginRequest.getUsername());
        if (user == null) {
            log.warn("User not found with username: {}", loginRequest.getUsername());
            user = userRepository.findByEmail(loginRequest.getUsername());
        }

        if (user == null) {
            log.error("User not found for username or email: {}", loginRequest.getUsername());
            throw new Exception("User not found");
        }

        log.info("User found: {}", user.getUsername());

        if (!user.isVerified()) {
            log.warn("User '{}' tried to login but is not verified.", user.getUsername());
            throw new Exception("Account not verified. Please check your email for the verification link.");
        }

        try {
            log.info("Authenticating user: {}", user.getUsername());
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getUsername(), loginRequest.getPassword())
            );
        } catch (BadCredentialsException e) {
            log.error("Invalid credentials for user: {}", user.getUsername());
            throw new Exception("Invalid username or password", e);
        }

        final UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
        final String token = jwtUtil.generateToken(userDetails);

        log.info("User '{}' successfully authenticated. Token generated.", user.getUsername());

        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setToken(token);
        loginResponse.setUsername(user.getUsername());
        loginResponse.setAccountType(user.getAccountType().name());
        loginResponse.setRoles(user.getRoles().stream().map(Role::getName).collect(Collectors.toSet()));

        Map<String, Set<String>> featurePrivileges = new HashMap<>();
		user.getRoles().forEach(role -> role.getPrivileges().forEach(privilege -> {
			String moduleName = privilege.getModuleName().name().toUpperCase();

			Set<String> actions = featurePrivileges.computeIfAbsent(moduleName, k -> new HashSet<>());

			
			log.info("Privilege for module: {}, canCreate: {}, canRead: {}, canUpdate: {}, canDelete: {}",
				    privilege.getModuleName(), 
				    privilege.isCanCreate(), 
				    privilege.isCanRead(), 
				    privilege.isCanUpdate(), 
				    privilege.isCanDelete());

			if (!privilege.isCanCreate())
				actions.add("CREATE");
			if (!privilege.isCanRead())
				actions.add("READ");
			if (!privilege.isCanUpdate())
				actions.add("UPDATE");
			if (!privilege.isCanDelete())
				actions.add("DELETE");
		}));
		log.info("featurePrivileges built: {}", featurePrivileges);


        
		
        loginResponse.setFeaturePrivileges(featurePrivileges);

        if (user.getOrganization() != null) {
            LoginResponse.OrganizationDTO orgDTO = new LoginResponse.OrganizationDTO();
            orgDTO.setName(user.getOrganization().getName());
            orgDTO.setAddress(user.getOrganization().getAddress());
            orgDTO.setLawFirmData(user.getOrganization().getLawFirmData());
            loginResponse.setOrganization(orgDTO);
        }

        log.info("Login successful for user: {}", user.getUsername());
        return loginResponse;
    }
    

    @Override
    @Transactional
    public void forgotPassword(String email) throws Exception {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new Exception("No user found with this email");
        }

        String token = UUID.randomUUID().toString();
        ResetToken resetToken = new ResetToken();
        resetToken.setToken(token);
        resetToken.setUser(user);
        resetTokenRepository.save(resetToken);

        String resetLink = "http://localhost:8085/api/auth/reset-password?token=" + token;
        sendEmail(user.getEmail(), "Password Reset Request", 
            "Click the link to reset your password: <a href=\"" + resetLink + "\">Reset Password</a>");
    }

    @Override
    @Transactional
    public void updatePassword(String token, String newPassword) throws Exception {
        ResetToken resetToken = resetTokenRepository.findByToken(token);
        if (resetToken == null || resetToken.isExpired()) {
            throw new Exception("Invalid or expired token");
        }

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        resetTokenRepository.delete(resetToken); // Invalidate token
    }

    @Override
    @Transactional
    public void resetPassword(String email) throws Exception {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new Exception("No user found with this email");
        }

        String token = UUID.randomUUID().toString();
        ResetToken resetToken = new ResetToken();
        resetToken.setToken(token);
        resetToken.setUser(user);
        resetTokenRepository.save(resetToken);

        String resetLink = "http://localhost:8085/api/auth/reset-password?token=" + token;
        sendEmail(user.getEmail(), "Password Reset Request", 
            "Click the link to reset your password: <a href=\"" + resetLink + "\">Reset Password</a>");
    }

    private void sendEmail(String to, String subject, String body) throws Exception {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(body, true); // true indicates HTML content
        mailSender.send(message);
    }
    
    @Override
    @Transactional
    public void updatePasswordForLoggedInUser(String username, String currentPassword, String newPassword) throws Exception {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new Exception("User not found");
        }

        // Verify current password
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new Exception("Current password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
}