package com.zephsie.wellbeing.models.entity;

import com.zephsie.wellbeing.models.api.IImmutableEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "verification_token", schema = "structure",
        indexes = {
                @Index(name = "verification_token_token_idx", columnList = "token"),
                @Index(name = "verification_token_user_id_idx", columnList = "user_id")
        }
)
@NoArgsConstructor
public class VerificationToken implements IImmutableEntity<UUID> {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    @Access(AccessType.PROPERTY)
    @Getter
    @Setter
    private UUID id;

    @Column(name = "token", nullable = false)
    @Access(AccessType.PROPERTY)
    @Getter
    @Setter
    @NotEmpty(message = "Token cannot be empty")
    private String token;
  
    @OneToOne(targetEntity = User.class, fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    @JoinColumn(nullable = false, name = "user_id", updatable = false)
    @Access(AccessType.PROPERTY)
    @Getter
    @Setter
    private User user;

    @Column(name = "create_date", columnDefinition = "TIMESTAMP", precision = 3)
    @Access(AccessType.FIELD)
    @CreationTimestamp
    @Getter
    private LocalDateTime createDate;

    public VerificationToken(String token, User user) {
        this.token = token;
        this.user = user;
    }
}