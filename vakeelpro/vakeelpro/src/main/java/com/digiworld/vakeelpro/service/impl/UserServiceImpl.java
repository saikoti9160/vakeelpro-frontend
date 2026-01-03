package com.digiworld.vakeelpro.service.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.digiworld.vakeelpro.constants.AccountType;
import com.digiworld.vakeelpro.dto.SignupRequest;
import com.digiworld.vakeelpro.dto.SignupResponse;
import com.digiworld.vakeelpro.entities.Organization;
import com.digiworld.vakeelpro.entities.Role;
import com.digiworld.vakeelpro.entities.User;
import com.digiworld.vakeelpro.entities.VerificationToken;
import com.digiworld.vakeelpro.repositories.OrganizationRepository;
import com.digiworld.vakeelpro.repositories.RoleRepository;
import com.digiworld.vakeelpro.repositories.UserRepository;
import com.digiworld.vakeelpro.repositories.VerificationTokenRepository;
import com.digiworld.vakeelpro.service.UserService;

import jakarta.mail.internet.MimeMessage;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

  
    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private VerificationTokenRepository verificationTokenRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JavaMailSender mailSender;

    
    @Value("${from.email.address}") // Load from properties securely
    private String fromEmail;

    @Override
    @Transactional
    public User createUser(User user, Set<String> roleNames) {
        // Encode password
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Assign roles
        Set<Role> roles = fetchRolesByNames(roleNames);
        user.setRoles(roles);

        return userRepository.save(user);
    }

    @Override
    @Transactional
    public User updateUser(Long userId, User userDetails, Set<String> roleNames) {
        User existingUser = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        // Update fields
        existingUser.setUsername(userDetails.getUsername());
        existingUser.setEmail(userDetails.getEmail());
        if (userDetails.getPassword() != null && !userDetails.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(userDetails.getPassword()));
        }
        existingUser.setAccountType(userDetails.getAccountType());

        // Update roles if provided
        if (roleNames != null && !roleNames.isEmpty()) {
            Set<Role> roles = fetchRolesByNames(roleNames);
            existingUser.setRoles(roles);
        }

        return userRepository.save(existingUser);
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        userRepository.delete(user);
    }

    @Override
    public User getUserById(Long userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
    }

    @Override
    public User getUserByUsername(String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("User not found with username: " + username);
        }
        return user;
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    @Transactional
    public void assignRolesToUser(Long userId, Set<String> roleNames) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        Set<Role> roles = fetchRolesByNames(roleNames);
        user.setRoles(roles);
        userRepository.save(user);
    }


    private Set<Role> fetchRolesByNames(Set<String> roleNames) {
        if (roleNames == null || roleNames.isEmpty()) {
            return new HashSet<>();
        }
        Set<Role> roles = roleNames.stream()
            .map(roleRepository::findByName)
            .filter(role -> role != null)
            .collect(Collectors.toSet());
        if (roles.size() != roleNames.size()) {
            throw new RuntimeException("One or more roles not found");
        }
        return roles;
    }
    
 
    @Override
    @Transactional
    public User createStaff(Long adminUserId, User staff, Set<String> roleNames) {
        User admin = userRepository.findById(adminUserId)
            .orElseThrow(() -> new RuntimeException("Admin user not found"));

        if (!admin.getAccountType().equals(AccountType.ORGANIZATION) && 
            !admin.getAccountType().equals(AccountType.SUPER_ADMIN)) {
            throw new RuntimeException("Only ORGANIZATION or SUPER_ADMIN can create staff");
        }

        staff.setPassword(passwordEncoder.encode(staff.getPassword()));
        Set<Role> roles = fetchRolesByNames(roleNames);
        staff.setRoles(roles);

        // Associate staff with the admin's organization
        if (admin.getAccountType().equals(AccountType.ORGANIZATION)) {
            staff.setOrganization(admin.getOrganization());
        } else if (admin.getAccountType().equals(AccountType.SUPER_ADMIN) && admin.getOrganization() != null) {
            staff.setOrganization(admin.getOrganization());
        }

        return userRepository.save(staff);
    }

    @Override
    @Transactional
    public void makeStaffAdmin(Long adminUserId, Long staffId) {
        User admin = userRepository.findById(adminUserId)
            .orElseThrow(() -> new RuntimeException("Admin user not found"));
        
        if (!admin.getAccountType().equals(AccountType.ORGANIZATION) && 
            !admin.getAccountType().equals(AccountType.SUPER_ADMIN)) {
            throw new RuntimeException("Only ORGANIZATION or SUPER_ADMIN can make staff admin");
        }

        User staff = userRepository.findById(staffId)
            .orElseThrow(() -> new RuntimeException("Staff user not found"));

        if (staff.getOrganization() == null || 
            !staff.getOrganization().equals(admin.getOrganization())) {
            throw new RuntimeException("Staff must belong to the same organization as the admin");
        }

        staff.setAdmin(true);
        userRepository.save(staff);
    }
    
    @Override
    @Transactional
    public SignupResponse signup(SignupRequest signupRequest) {
        // Validate account type
        AccountType accountType;
        try {
            accountType = AccountType.valueOf(signupRequest.getAccountType().toUpperCase());
            if (accountType != AccountType.INDIVIDUAL && accountType != AccountType.ORGANIZATION) {
                throw new IllegalArgumentException("Account type must be INDIVIDUAL or ORGANIZATION");
            }
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid account type. Must be INDIVIDUAL or ORGANIZATION");
        }

        // Check for existing user
        if (userRepository.findByUsername(signupRequest.getUsername()) != null) {
            throw new RuntimeException("Username already exists");
        }
        if (userRepository.findByEmail(signupRequest.getEmail()) != null) {
            throw new RuntimeException("Email already exists");
        }

        // Create user
        User user = new User();
        user.setUsername(signupRequest.getUsername());
        user.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
        user.setEmail(signupRequest.getEmail());
        user.setAccountType(accountType);
        user.setRoles(new HashSet<>());
        user.setVerified(false);

        // Assign roles based on account type
        if (accountType == AccountType.ORGANIZATION) {
            if (signupRequest.getOrganization() == null) {
                throw new RuntimeException("Organization data is required for ORGANIZATION account type");
            }

            Organization organization = new Organization();
            organization.setName(signupRequest.getOrganization().getName());
            organization.setAddress(signupRequest.getOrganization().getAddress());
            organization.setLawFirmData(signupRequest.getOrganization().getLawFirmData());
            organization.setStaff(new HashSet<>());
            organization.setCases(new HashSet<>());
            organizationRepository.save(organization);

            user.setOrganization(organization);
            user.setAdmin(true);
            user.getRoles().add(roleRepository.findByName("ADMIN")); // ADMIN role for ORGANIZATION
        } else if (accountType == AccountType.INDIVIDUAL) {
            Role individualRole = roleRepository.findByName("USER");
            if (individualRole == null) {
                throw new RuntimeException("INDIVIDUAL role not found. Please ensure initial data setup.");
            }
            user.getRoles().add(individualRole); // Assign default INDIVIDUAL role
        }

        userRepository.save(user);

        // Generate and save verification token
        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(token);
        verificationToken.setUser(user);
        verificationTokenRepository.save(verificationToken);

        // Send verification email
        String verificationLink = "http://localhost:8085/api/auth/verify?token=" + token;
        try {
            sendVerificationEmail(user.getEmail(), verificationLink);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send verification email: " + e.getMessage());
        }

        // Prepare response
        SignupResponse response = new SignupResponse();
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setAccountType(user.getAccountType().name());
        response.setMessage("User registered successfully. Please check your email to verify your account.");

        return response;
    }

    @Override
    @Transactional
    public void verifyAccount(String token) {
        VerificationToken verificationToken = verificationTokenRepository.findByToken(token);
        if (verificationToken == null || verificationToken.isExpired()) {
            throw new RuntimeException("Invalid or expired verification token");
        }

        User user = verificationToken.getUser();
        if (user.isVerified()) {
            throw new RuntimeException("Account is already verified");
        }

        user.setVerified(true);
        userRepository.save(user);
        verificationTokenRepository.delete(verificationToken); // Clean up token
    }
    @Override
    public void sendVerificationEmail(String email, String verificationLink) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom(fromEmail); // Use verified email
            helper.setTo(email);
            helper.setSubject("Verify Your VakeelPro Account");

            String emailContent = String.format(
                "<html><body>" +
                "<p>Please click the link below to verify your account:</p>" +
                "<p><a href='%s' style='color:blue; text-decoration:none; font-weight:bold;'>Verify Account</a></p>" +
                "<p>This link will expire in 24 hours.</p>" +
                "</body></html>", verificationLink
            );

            helper.setText(emailContent, true);
            mailSender.send(message);

            System.out.println("Verification email sent to: " + email);
        } catch (Exception e) {
            System.err.println("Error sending verification email: " + e.getMessage());
        }
    }

   
}