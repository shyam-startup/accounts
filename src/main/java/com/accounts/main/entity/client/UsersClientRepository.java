package com.accounts.main.entity.client;

import com.accounts.main.entity.users.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UsersClientRepository extends JpaRepository<UsersClient, Long> {

    @Query("SELECT uc FROM UsersClient uc JOIN FETCH uc.client WHERE uc.user = :user")
    List<UsersClient> findByUserWithClient(@Param("user") Users user);
}
