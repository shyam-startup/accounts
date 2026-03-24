package com.accounts.main.controller;

import com.accounts.main.controller.dto.AuthorizeResponse;
import com.accounts.main.controller.dto.OAuthTokenResponse;
import com.accounts.main.controller.dto.TokenRequest;
import com.accounts.main.controller.dto.TokenValidationResponse;
import com.accounts.main.service.OAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/oauth")
@RequiredArgsConstructor
public class OAuthController {

    private static final String COOKIE_NAME = "sessionToken";

    private final OAuthService oAuthService;

    @GetMapping("/authorize")
    public ResponseEntity<AuthorizeResponse> authorize(
            @RequestParam("client_id") String clientId,
            @CookieValue(name = COOKIE_NAME, required = false) String sessionToken) {

        if (sessionToken == null) {
            return ResponseEntity.status(401).build();
        }

        AuthorizeResponse response = oAuthService.generateCode(clientId, sessionToken);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/token")
    public ResponseEntity<OAuthTokenResponse> token(@RequestBody TokenRequest request) {
        OAuthTokenResponse response = oAuthService.exchangeToken(
                request.getClientId(), request.getClientSecret(), request.getCode());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/token/validate")
    public ResponseEntity<TokenValidationResponse> validate(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.ok(TokenValidationResponse.builder()
                    .valid(false)
                    .reason("Missing or malformed Authorization header")
                    .build());
        }

        TokenValidationResponse result = oAuthService.validateToken(authHeader.substring(7));
        return ResponseEntity.ok(result);
    }
}
