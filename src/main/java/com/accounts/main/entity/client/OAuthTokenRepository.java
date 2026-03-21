package com.accounts.main.entity.client;

import com.accounts.main.entity.users.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OAuthTokenRepository extends JpaRepository<OAuthToken, String> {

    Optional<OAuthToken> findByAccessToken(String accessToken);

    Optional<OAuthToken> findByUserAndClient(Users user, Client client);
}
