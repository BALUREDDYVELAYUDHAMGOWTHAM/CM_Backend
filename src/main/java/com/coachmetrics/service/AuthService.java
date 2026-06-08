package com.coachmetrics.service;

import com.coachmetrics.dto.AuthDto;
import com.coachmetrics.entity.User;
import com.coachmetrics.exception.ResourceNotFoundException;
import com.coachmetrics.repository.UserRepository;
import com.coachmetrics.security.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private ApplicationContext applicationContext;

    private AuthenticationManager getAuthManager() {
        return applicationContext.getBean(AuthenticationManager.class);
    }

    public AuthDto.AuthResponse login(AuthDto.LoginRequest req) {
        getAuthManager().authenticate(
                new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword()));

        User user = userRepo.findByEmail(req.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        String token = jwtService.generateToken(user);

        AuthDto.AuthResponse response = new AuthDto.AuthResponse();
        response.setToken(token);
        response.setRole(user.getRole().name());
        response.setFullName(user.getFullName());
        response.setUserId(user.getId());
        response.setEmail(user.getEmail());
        response.setDepartment(user.getDepartment() != null ? user.getDepartment().name() : null);
        return response;
    }
}
