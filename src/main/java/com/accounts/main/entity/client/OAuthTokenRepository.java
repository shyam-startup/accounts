package com.accounts.main.entity.client;

import com.accounts.main.entity.session.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OAuthTokenRepository extends JpaRepository<OAuthToken, String> {

    Optional<OAuthToken> findByAccessToken(String accessToken);

    List<OAuthToken> findBySession(Session session);

    Optional<OAuthToken> findBySessionAndClient(Session session, Client client);

    @Modifying
    @Query("DELETE FROM OAuthToken t WHERE t.accessTokenExpiresAt < :now AND t.revoked = true")
    void deleteExpiredAndRevoked(@Param("now") LocalDateTime now);
}
