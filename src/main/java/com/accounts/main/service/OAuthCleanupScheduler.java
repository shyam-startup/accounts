package com.accounts.main.service;

import com.accounts.main.entity.client.OAuthTempCodesRepository;
import com.accounts.main.entity.client.OAuthTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class OAuthCleanupScheduler {

    private final OAuthTempCodesRepository tempCodesRepository;
    private final OAuthTokenRepository tokenRepository;

    @Scheduled(fixedRate = 3_600_000)
    @Transactional
    public void cleanupExpiredData() {
        LocalDateTime now = LocalDateTime.now();
        tempCodesRepository.deleteAllByExpiresAtBefore(now);
        tokenRepository.deleteExpiredAndRevoked(now);
    }
}
