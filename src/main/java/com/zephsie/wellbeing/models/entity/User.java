package com.zephsie.wellbeing.models.entity;

import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.zephsie.wellbeing.models.api.IBaseEntity;
import com.zephsie.wellbeing.utils.serializers.CustomLocalDateTimeDesSerializer;
import com.zephsie.wellbeing.utils.serializers.CustomLocalDateTimeSerializer;
import com.zephsie.wellbeing.utils.views.UserView;
import jakarta.persistence.*;
import lombok.*;
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
public class User implements IBaseEntity<UUID> {

    @Id
    @Column(name = "id")
    @JsonView(UserView.Min.class)
    @Access(AccessType.PROPERTY)
    @Getter
    @Setter
    private UUID id = UUID.randomUUID();

    @Column(name = "username", nullable = false)
    @Access(AccessType.PROPERTY)
    @JsonView(UserView.Min.class)
    @Getter
    @Setter
    private String username;

    @Column(name = "email", nullable = false, unique = true)
    @Access(AccessType.PROPERTY)
    @JsonView(UserView.Min.class)
    @Getter
    @Setter
    private String email;

    @Column(name = "password", nullable = false)
    @Access(AccessType.PROPERTY)
    @Getter
    @Setter
    private String password;

    @Column(name = "role", nullable = false)
    @Access(AccessType.PROPERTY)
    @JsonView(UserView.Min.class)
    @Getter
    @Setter
    private String role;

    @Column(name = "is_active", nullable = false)
    @Access(AccessType.PROPERTY)
    @JsonView(UserView.Min.class)
    @Getter
    @Setter
    private Boolean isActive;

    @Version
    @Column(name = "version", columnDefinition = "TIMESTAMP", precision = 3)
    @Access(AccessType.FIELD)
    @JsonView(UserView.Min.class)
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDesSerializer.class)
    @Getter
    private LocalDateTime version;

    @Column(name = "create_date", columnDefinition = "TIMESTAMP", precision = 3)
    @Access(AccessType.FIELD)
    @JsonView(UserView.Min.class)
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDesSerializer.class)
    @CreationTimestamp
    @Getter
    private LocalDateTime createDate;
}