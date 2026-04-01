package com.accounts.main.entity.client;

import com.accounts.main.entity.session.Session;
import com.accounts.main.entity.users.Users;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "oauth_temp_codes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OAuthTempCodes {

    @Id
    @Column(length = 36)
    private String id;

    @Column(nullable = false, unique = true, length = 512)
    private String code;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "session_id", nullable = false)
    private Session session;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(nullable = false)
    @Builder.Default
    private boolean used = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (id == null) id = UUID.randomUUID().toString();
        createdAt = LocalDateTime.now();
    }
}
