package com.accounts.main.entity.client;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OAuthTempCodesRepository extends JpaRepository<OAuthTempCodes, String> {

    Optional<OAuthTempCodes> findByCode(String code);
}
