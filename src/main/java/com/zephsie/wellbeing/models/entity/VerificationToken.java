package com.zephsie.wellbeing.models.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "verification_token", schema = "structure",
        indexes = {
                @Index(name = "verification_token_token_idx", columnList = "token"),
                @Index(name = "verification_token_user_id_idx", columnList = "user_id")
        }
)
@Getter
@Setter
@NoArgsConstructor
public class VerificationToken {
    @Id
    @Column(name = "id")
    @Access(AccessType.PROPERTY)
    @Getter
    @Setter
    private UUID id = UUID.randomUUID();

    @Column(name = "token", nullable = false)
    @Access(AccessType.PROPERTY)
    @Getter
    @Setter
    @NotEmpty(message = "Token cannot be empty")
    private String token;
  
    @OneToOne(targetEntity = User.class, fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    @JoinColumn(nullable = false, name = "user_id", unique = true, updatable = false)
    @Access(AccessType.PROPERTY)
    @Getter
    @Setter
    private User user;

    public VerificationToken(String token, User user) {
        this.token = token;
        this.user = user;
    }
}