package com.accounts.main.entity.session;

import com.accounts.main.entity.users.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SessionRepository extends JpaRepository<Session, String> {

    Optional<Session> findBySessionToken(String sessionToken);

    Optional<Session> findByRefreshToken(String refreshToken);

    List<Session> findByUser(Users user);

    List<Session> findByUserAndActive(Users user, boolean active);

    void deleteByUser(Users user);
}
