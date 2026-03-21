package com.accounts.main.entity.client;

import com.accounts.main.entity.users.Users;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users_clients",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "client_id"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsersClient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @Column(name = "consented_at", nullable = false, updatable = false)
    private LocalDateTime consentedAt;

    @PrePersist
    protected void onCreate() {
        consentedAt = LocalDateTime.now();
    }
}
