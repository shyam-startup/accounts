package com.accounts.main.entity.client;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface OAuthTempCodesRepository extends JpaRepository<OAuthTempCodes, String> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<OAuthTempCodes> findByCode(String code);

    void deleteAllByExpiresAtBefore(LocalDateTime time);
}
