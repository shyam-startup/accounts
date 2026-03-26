package com.accounts.main.entity.client;

import com.accounts.main.entity.session.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OAuthTokenRepository extends JpaRepository<OAuthToken, String> {

    Optional<OAuthToken> findByAccessToken(String accessToken);

    List<OAuthToken> findBySession(Session session);

    Optional<OAuthToken> findBySessionAndClient(Session session, Client client);
}
