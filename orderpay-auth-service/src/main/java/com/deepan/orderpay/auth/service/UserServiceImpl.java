package com.deepan.orderpay.auth.service;

import com.deepan.orderpay.auth.dto.RegisterRequest;
import com.deepan.orderpay.auth.dto.UserResponse;
import com.deepan.orderpay.auth.entity.Role;
import com.deepan.orderpay.auth.entity.User;
import com.deepan.orderpay.auth.exception.DuplicateResourceException;
import com.deepan.orderpay.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public UserResponse register(RegisterRequest request) {
        String email = request.email().trim().toLowerCase(Locale.ROOT);

        if (userRepository.existsByEmail(email)) {
            throw new DuplicateResourceException("User", "email", email);
        }

        User user = new User(
                email,
                passwordEncoder.encode(request.password()),
                request.fullName().trim(),
                Set.of(Role.USER));                        // always USER, never taken from the request

        try {
            user = userRepository.saveAndFlush(user);      // flush now so a constraint violation surfaces here
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateResourceException("User", "email", email);   // lost a race with another request
        }
        return UserResponse.from(user);
    }
}