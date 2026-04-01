package com.accounts.main.service;

import com.accounts.main.controller.dto.AuthorizeResponse;
import com.accounts.main.controller.dto.OAuthTokenResponse;
import com.accounts.main.controller.dto.TokenValidationResponse;
import com.accounts.main.controller.dto.UserInfoResponse;
import com.accounts.main.entity.client.Client;
import com.accounts.main.entity.client.ClientRepository;
import com.accounts.main.entity.client.OAuthTempCodes;
import com.accounts.main.entity.client.OAuthTempCodesRepository;
import com.accounts.main.entity.client.OAuthToken;
import com.accounts.main.entity.client.OAuthTokenRepository;
import com.accounts.main.entity.client.UsersClient;
import com.accounts.main.entity.client.UsersClientRepository;
import com.accounts.main.entity.session.Session;
import com.accounts.main.entity.session.SessionRepository;
import com.accounts.main.entity.users.Users;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OAuthService {

    private static final int CODE_EXPIRY_MINUTES = 5;
    private static final int ACCESS_TOKEN_EXPIRY_HOURS = 1;
    private static final int REFRESH_TOKEN_EXPIRY_DAYS = 30;

    private final ClientRepository clientRepository;
    private final OAuthTempCodesRepository tempCodesRepository;
    private final OAuthTokenRepository tokenRepository;
    private final UsersClientRepository usersClientRepository;
    private final SessionRepository sessionRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Transactional
    public AuthorizeResponse generateCode(String clientId, String sessionToken) {
        Session session = sessionRepository.findBySessionToken(sessionToken)
                .orElseThrow(() -> new IllegalStateException("Invalid session"));

        if (!session.isActive() || session.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Session expired");
        }

        Client client = clientRepository.findByClientId(clientId)
                .orElseThrow(() -> new IllegalArgumentException("Unknown client: " + clientId));

        if (!client.isEnabled()) {
            throw new IllegalStateException("Client is disabled");
        }

        Users user = session.getUser();

        OAuthTempCodes tempCode = OAuthTempCodes.builder()
                .code(UUID.randomUUID().toString())
                .user(user)
                .client(client)
                .session(session)
                .expiresAt(LocalDateTime.now().plusMinutes(CODE_EXPIRY_MINUTES))
                .build();

        tempCodesRepository.save(tempCode);

        String redirectUrl = client.getRedirectUri() + "?code=" + tempCode.getCode();
        return AuthorizeResponse.builder().redirectUrl(redirectUrl).build();
    }

    @Transactional
    public OAuthTokenResponse exchangeToken(String clientId, String clientSecret, String code) {
        Client client = clientRepository.findByClientId(clientId)
                .orElseThrow(() -> new IllegalArgumentException("Unknown client"));

        if (!client.getClientSecret().equals(clientSecret)) {
            throw new IllegalArgumentException("Invalid client credentials");
        }

        OAuthTempCodes tempCode = tempCodesRepository.findByCode(code)
                .orElseThrow(() -> new IllegalArgumentException("Invalid authorization code"));

        if (tempCode.isUsed()) {
            throw new IllegalArgumentException("Authorization code already used");
        }

        if (tempCode.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Authorization code has expired");
        }

        if (!tempCode.getClient().getClientId().equals(clientId)) {
            throw new IllegalArgumentException("Code was not issued for this client");
        }

        tempCode.setUsed(true);
        tempCodesRepository.save(tempCode);

        Users user = tempCode.getUser();

        OAuthToken token = OAuthToken.builder()
                .user(user)
                .client(client)
                .session(tempCode.getSession())
                .accessToken(UUID.randomUUID().toString())
                .refreshToken(UUID.randomUUID().toString())
                .accessTokenExpiresAt(LocalDateTime.now().plusHours(ACCESS_TOKEN_EXPIRY_HOURS))
                .refreshTokenExpiresAt(LocalDateTime.now().plusDays(REFRESH_TOKEN_EXPIRY_DAYS))
                .build();
        tokenRepository.save(token);

        boolean alreadyConnected = usersClientRepository.findByUserWithClient(user)
                .stream().anyMatch(uc -> uc.getClient().getClientId().equals(clientId));
        if (!alreadyConnected) {
            usersClientRepository.save(UsersClient.builder().user(user).client(client).build());
        }

        UserInfoResponse userInfo = UserInfoResponse.builder()
                .userId(user.getId().toString())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .enabled(user.isEnabled())
                .isConfirmed(user.isConfirmed())
                .createdAt(user.getCreatedAt())
                .build();

        return OAuthTokenResponse.builder()
                .accessToken(token.getAccessToken())
                .refreshToken(token.getRefreshToken())
                .expiresAt(token.getAccessTokenExpiresAt())
                .user(userInfo)
                .build();
    }

    @Transactional(readOnly = true)
    public UserInfoResponse getUserInfo(String accessToken) {
        OAuthToken token = tokenRepository.findByAccessToken(accessToken)
                .orElseThrow(() -> new IllegalArgumentException("Invalid access token"));

        if (token.isRevoked()) {
            throw new IllegalStateException("Access token has been revoked");
        }

        if (token.getAccessTokenExpiresAt().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Access token has expired");
        }

        Users user = token.getUser();
        return UserInfoResponse.builder()
                .userId(user.getId().toString())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .build();
    }

    @Transactional(readOnly = true)
    public TokenValidationResponse validateToken(String accessToken) {
        OAuthToken token = tokenRepository.findByAccessToken(accessToken).orElse(null);

        if (token == null) {
            return TokenValidationResponse.builder().valid(false).reason("Invalid access token").build();
        }

        if (token.isRevoked()) {
            return TokenValidationResponse.builder().valid(false).reason("Access token has been revoked").build();
        }

        if (token.getAccessTokenExpiresAt().isBefore(LocalDateTime.now())) {
            return TokenValidationResponse.builder().valid(false).reason("Access token has expired").build();
        }

        Users user = token.getUser();
        UserInfoResponse userInfo = UserInfoResponse.builder()
                .userId(user.getId().toString())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .build();

        return TokenValidationResponse.builder()
                .valid(true)
                .user(userInfo)
                .expiresAt(token.getAccessTokenExpiresAt())
                .build();
    }

    @Transactional
    public void revokeSessionTokens(Session session) {
        tokenRepository.findBySession(session).forEach(t -> t.setRevoked(true));
    }
}
