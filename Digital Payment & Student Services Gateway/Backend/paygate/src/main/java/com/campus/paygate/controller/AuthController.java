package com.campus.paygate.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.campus.paygate.dto.AuthDTO;
import com.campus.paygate.dto.RegisterRequest;
import com.campus.paygate.model.User;
import com.campus.paygate.repository.UserRepository;
import com.campus.paygate.security.JwtTokenProvider;

@RestController
@RequestMapping("/api/v1/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final UserRepository userRepository;
    private final JwtTokenProvider tokenProvider;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UserRepository userRepository, JwtTokenProvider tokenProvider, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.tokenProvider = tokenProvider;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequest request) {
        
        // 1. Check if the matric number is already registered
        if (userRepository.findUserByIdentifier(request.getMatricOrStaffId()).isPresent()) {
            return ResponseEntity.badRequest().body("Error: Matriculation Number is already in use!");
        }

        // 2. Create the new user
        User user = new User();
        
        // Ensure this setter matches your User.java model exactly! 
        user.setMatricNo(request.getMatricOrStaffId()); 
        user.setEmail(request.getEmail());
        user.setRole("STUDENT"); // Default role
        
        // 3. Secure the password before saving
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        // 4. Save to MongoDB
        userRepository.save(user);

        return ResponseEntity.ok("Student registered successfully!");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthDTO.LoginRequest request) {
        
        User user = userRepository.findUserByIdentifier(request.getMatricOrStaffId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return ResponseEntity.badRequest().body("Invalid credentials");
        }

        String identifier = user.getMatricNo() != null ? user.getMatricNo() : user.getStaffId();
        String token = tokenProvider.generateToken(identifier, user.getRole());

        return ResponseEntity.ok(new AuthDTO.AuthResponse(token, identifier, user.getRole()));
    }
}