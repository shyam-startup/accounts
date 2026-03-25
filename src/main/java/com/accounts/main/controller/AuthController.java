package com.accounts.main.controller;

import com.accounts.main.controller.dto.AvailableAppResponse;
import com.accounts.main.controller.dto.ConnectedAppResponse;
import com.accounts.main.controller.dto.SigninRequest;
import com.accounts.main.controller.dto.SignupRequest;
import com.accounts.main.controller.dto.UserResponse;
import com.accounts.main.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.Duration;
import java.util.List;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private static final String COOKIE_NAME = "sessionToken";
    private static final Duration COOKIE_MAX_AGE = Duration.ofHours(1);

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<Void> signup(@RequestBody SignupRequest request,
                                       HttpServletRequest httpRequest,
                                       HttpServletResponse httpResponse) {
        String sessionToken = authService.signup(
                request, httpRequest.getRemoteAddr(), httpRequest.getHeader("User-Agent"));
        setSessionCookie(httpResponse, sessionToken);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/signin")
    public ResponseEntity<Void> signin(@RequestBody SigninRequest request,
                                       HttpServletRequest httpRequest,
                                       HttpServletResponse httpResponse) {
        String sessionToken = authService.signin(
                request, httpRequest.getRemoteAddr(), httpRequest.getHeader("User-Agent"));
        setSessionCookie(httpResponse, sessionToken);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/signout")
    public ResponseEntity<Void> signout(
            @CookieValue(name = COOKIE_NAME, required = false) String sessionToken,
            HttpServletResponse httpResponse) {
        if (sessionToken != null) {
            authService.signout(sessionToken);
        }
        clearSessionCookie(httpResponse);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> me(@CookieValue(name = COOKIE_NAME, required = false) String sessionToken) {
        if (sessionToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        UserResponse user = authService.getCurrentUser(sessionToken);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/connected-apps")
    public ResponseEntity<List<ConnectedAppResponse>> connectedApps(
            @CookieValue(name = COOKIE_NAME, required = false) String sessionToken) {
        if (sessionToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(authService.getConnectedApps(sessionToken));
    }

    @GetMapping("/available-apps")
    public ResponseEntity<List<AvailableAppResponse>> availableApps() {
        return ResponseEntity.ok(authService.getAvailableApps());
    }

    private void setSessionCookie(HttpServletResponse response, String token) {
        ResponseCookie cookie = ResponseCookie.from(COOKIE_NAME, token)
                .httpOnly(true)
                .path("/")
                .sameSite("Strict")
                .maxAge(COOKIE_MAX_AGE)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    private void clearSessionCookie(HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from(COOKIE_NAME, "")
                .httpOnly(true)
                .path("/")
                .sameSite("Strict")
                .maxAge(Duration.ZERO)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }
}
