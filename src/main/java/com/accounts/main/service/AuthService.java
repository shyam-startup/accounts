package com.accounts.main.service;

import com.accounts.main.controller.dto.AuthResponse;
import com.accounts.main.controller.dto.SigninRequest;
import com.accounts.main.controller.dto.SignupRequest;
import com.accounts.main.controller.dto.UserResponse;
import com.accounts.main.entity.session.Session;
import com.accounts.main.entity.session.SessionRepository;
import com.accounts.main.entity.users.Users;
import com.accounts.main.entity.users.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private static final int SESSION_EXPIRY_HOURS = 1;

    private final UsersRepository usersRepository;
    private final SessionRepository sessionRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Transactional
    public String signup(SignupRequest request, String ipAddress, String userAgent) {
        if (usersRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username already taken");
        }
        if (usersRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already registered");
        }

        Users user = Users.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .build();

        usersRepository.save(user);
        return createSession(user, ipAddress, userAgent);
    }

    @Transactional
    public String signin(SigninRequest request, String ipAddress, String userAgent) {
        Users user = usersRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Invalid username or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid username or password");
        }

        if (!user.isEnabled()) {
            throw new IllegalStateException("Account is disabled");
        }

        user.setLastLoginAt(LocalDateTime.now());
        usersRepository.save(user);

        return createSession(user, ipAddress, userAgent);
    }

    @Transactional(readOnly = true)
    public UserResponse getCurrentUser(String sessionToken) {
        Session session = sessionRepository.findBySessionToken(sessionToken)
                .orElseThrow(() -> new IllegalStateException("Invalid session"));

        if (!session.isActive() || session.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Session expired");
        }

        Users user = session.getUser();
        return UserResponse.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .build();
    }

    @Transactional
    public void signout(String sessionToken) {
        sessionRepository.findBySessionToken(sessionToken).ifPresent(session -> {
            session.setActive(false);
            sessionRepository.save(session);
        });
    }

    private String createSession(Users user, String ipAddress, String userAgent) {
        Session session = Session.builder()
                .user(user)
                .sessionToken(UUID.randomUUID().toString())
                .refreshToken(UUID.randomUUID().toString())
                .expiresAt(LocalDateTime.now().plusHours(SESSION_EXPIRY_HOURS))
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .build();

        sessionRepository.save(session);
        return session.getSessionToken();
    }
}
