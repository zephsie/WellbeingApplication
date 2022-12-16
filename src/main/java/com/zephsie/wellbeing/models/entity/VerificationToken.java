package com.zephsie.wellbeing.models.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Access(AccessType.PROPERTY)
    @Getter
    @Setter
    private Long id;

    @Column(name = "token")
    @Access(AccessType.PROPERTY)
    @Getter
    @Setter
    @NotEmpty(message = "Token cannot be empty")
    private String token;
  
    @OneToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "user_id")
    @Getter
    @Setter
    private User user;

    public VerificationToken(String token, User user) {
        this.token = token;
        this.user = user;
    }
}