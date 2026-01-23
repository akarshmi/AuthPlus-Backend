package com.auth.AuthPlus.entities;


import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "refreshTokens", indexes = {
        @Index(name = "refreshTokenJtiIdx",columnList = "jti",unique = true),
        @Index(name = "refreshTokenUserIdIdx",columnList = "userId")
})
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "jti", unique = true, nullable = false, updatable = false)
    private String jti;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", nullable = false, updatable = false)
    private User user;

    @Column(updatable = false,nullable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant expiresAt;

    @Column(nullable = false)
    private boolean revoked;

    private String replacedByToken;
}
