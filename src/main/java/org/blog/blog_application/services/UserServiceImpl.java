package org.blog.blog_application.services;

import org.blog.blog_application.dtos.RegistrationDto;
import org.blog.blog_application.models.Role;
import org.blog.blog_application.models.User;
import org.blog.blog_application.repositories.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    @Override
    public User register(RegistrationDto dto) {
        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new RuntimeException("Username already taken: " + dto.getUsername());
        }
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Email already registered: " + dto.getEmail());
        }

        User user = new User();
        user.setName(dto.getDisplayName());          // display name on posts
        user.setUsername(dto.getUsername());          // login credential
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));

        // Map the role string → enum safely, default to ROLE_USER
        Role role;
        try {
            role = Role.valueOf(dto.getRole());
        } catch (Exception e) {
            role = Role.ROLE_USER;
        }
        user.setRole(role);

        return userRepository.save(user);
    }

}
