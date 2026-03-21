package com.accounts.main.filter;

import com.accounts.main.entity.session.Session;
import com.accounts.main.entity.session.SessionRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;
import java.util.Set;

@Component
@Order(1)
@RequiredArgsConstructor
public class SessionAuthFilter extends OncePerRequestFilter {

    private static final String COOKIE_NAME = "sessionToken";

    private static final Set<String> PUBLIC_PATHS = Set.of(
            "/auth/signin",
            "/auth/signup",
            "/oauth/token",
            "/oauth/userinfo"
    );

    private final SessionRepository sessionRepository;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return PUBLIC_PATHS.contains(request.getRequestURI());
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String token = extractSessionToken(request);

        if (token == null) {
            sendUnauthorized(response, "No session token provided");
            return;
        }

        Optional<Session> sessionOpt = sessionRepository.findBySessionToken(token);

        if (sessionOpt.isEmpty()) {
            sendUnauthorized(response, "Invalid session token");
            return;
        }

        Session session = sessionOpt.get();

        if (!session.isActive()) {
            sendUnauthorized(response, "Session is no longer active");
            return;
        }

        if (session.getExpiresAt().isBefore(LocalDateTime.now())) {
            session.setActive(false);
            sessionRepository.save(session);
            sendUnauthorized(response, "Session has expired");
            return;
        }

        request.setAttribute("authenticatedUser", session.getUser());
        request.setAttribute("sessionId", session.getId());

        filterChain.doFilter(request, response);
    }

    private String extractSessionToken(HttpServletRequest request) {
        if (request.getCookies() == null) return null;
        return Arrays.stream(request.getCookies())
                .filter(c -> COOKIE_NAME.equals(c.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
    }

    private void sendUnauthorized(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write("{\"error\": \"" + message + "\"}");
    }
}
