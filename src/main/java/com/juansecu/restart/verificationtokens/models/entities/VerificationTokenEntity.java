package com.juansecu.restart.verificationtokens.models.entities;

import java.util.Date;
import java.util.UUID;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcType;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;

import com.juansecu.restart.users.models.entities.UserEntity;
import com.juansecu.restart.verificationtokens.enums.EVerificationTokenType;

@Entity(name = "Verification_tokens")
@Getter
@NoArgsConstructor
@RequiredArgsConstructor
@Setter
public class VerificationTokenEntity {
    @Column(name = "Verification_token_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Integer verificationTokenId;
    @Column(name = "Token", nullable = false)
    @NonNull
    private UUID token;
    @Column(name = "Type", nullable = false)
    @Enumerated(EnumType.STRING)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    @NonNull
    private EVerificationTokenType type;
    @Column(name = "Uses_count", nullable = false)
    private int usesCount = 0;
    @Column(name = "Metadata")
    private String metadata;
    @Column(name = "User_id", nullable = false)
    @NonNull
    private Integer userId;
    @Column(name = "Referenced_token_id")
    private Integer referencedTokenId;
    @Column(name = "Created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private Date createdAt;
    @Column(name = "Expires_at", nullable = false)
    @NonNull
    private Date expiresAt;
    @Column(name = "Updated_at", nullable = false)
    @UpdateTimestamp
    private Date updatedAt;
    @JoinColumn(
        insertable = false,
        name = "User_id",
        referencedColumnName = "User_id",
        updatable = false
    )
    @ManyToOne(fetch = FetchType.LAZY)
    private UserEntity user;

    public VerificationTokenEntity(
        final @NonNull UUID token,
        final @NonNull EVerificationTokenType type,
        final @NonNull String metadata,
        final @NonNull Date expiresAt,
        final @NonNull Integer userId
    ) {
        this.token = token;
        this.type = type;
        this.metadata = metadata;
        this.expiresAt = expiresAt;
        this.userId = userId;
    }
}
