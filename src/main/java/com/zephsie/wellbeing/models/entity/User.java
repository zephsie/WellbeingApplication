package com.zephsie.wellbeing.models.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.zephsie.wellbeing.models.api.IBaseEntity;
import com.zephsie.wellbeing.utils.serializers.CustomLocalDateTimeDesSerializer;
import com.zephsie.wellbeing.utils.serializers.CustomLocalDateTimeSerializer;
import com.zephsie.wellbeing.utils.views.UserView;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "user", schema = "structure",
        indexes = {
                @Index(name = "user_email_idx", columnList = "email"),
        }
)
@DynamicUpdate
@NoArgsConstructor
@JsonView(UserView.class)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class User implements IBaseEntity<UUID> {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    @Access(AccessType.PROPERTY)
    @Getter
    @Setter
    @JsonView(UserView.Minimal.class)
    private UUID id;

    @Column(name = "username", nullable = false)
    @Access(AccessType.PROPERTY)
    @Getter
    @Setter
    @JsonView(UserView.Minimal.class)
    private String username;

    @Column(name = "email", nullable = false, unique = true)
    @Access(AccessType.PROPERTY)
    @Getter
    @Setter
    @JsonView(UserView.Minimal.class)
    private String email;

    @Column(name = "password", nullable = false)
    @Access(AccessType.PROPERTY)
    @Getter
    @Setter
    private String password;

    @Column(name = "role", nullable = false)
    @Access(AccessType.PROPERTY)
    @Getter
    @Setter
    @JsonView(UserView.Minimal.class)
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(name = "is_active", nullable = false)
    @Access(AccessType.PROPERTY)
    @Getter
    @Setter
    @JsonView(UserView.Minimal.class)
    private Boolean isActive;

    @Version
    @Column(name = "version", columnDefinition = "TIMESTAMP", precision = 3)
    @Access(AccessType.FIELD)
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDesSerializer.class)
    @Getter
    @JsonView(UserView.System.class)
    private LocalDateTime version;

    @Column(name = "create_date", columnDefinition = "TIMESTAMP", precision = 3)
    @Access(AccessType.FIELD)
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDesSerializer.class)
    @CreationTimestamp
    @Getter
    @JsonView(UserView.System.class)
    private LocalDateTime createDate;
}