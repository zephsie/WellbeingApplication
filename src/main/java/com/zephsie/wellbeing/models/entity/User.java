package com.zephsie.wellbeing.models.entity;

import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.zephsie.wellbeing.utils.converters.CustomLocalDateTimeDesSerializer;
import com.zephsie.wellbeing.utils.converters.CustomLocalDateTimeSerializer;
import com.zephsie.wellbeing.utils.views.UserView;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;

@Entity
@Table(name = "user", schema = "structure",
        indexes = {
                @Index(name = "user_email_idx", columnList = "email"),
        }
)
@DynamicUpdate
@NoArgsConstructor
public class User {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView(UserView.Min.class)
    @Access(AccessType.PROPERTY)
    @Getter
    @Setter
    private Long id;

    @Column(name = "username")
    @Access(AccessType.PROPERTY)
    @JsonView(UserView.Min.class)
    @Getter
    @Setter
    private String username;

    @Column(name = "email")
    @Access(AccessType.PROPERTY)
    @JsonView(UserView.Min.class)
    @Getter
    @Setter
    private String email;

    @Column(name = "password")
    @Access(AccessType.PROPERTY)
    @Getter
    @Setter
    private String password;

    @Column(name = "role")
    @Access(AccessType.PROPERTY)
    @JsonView(UserView.Min.class)
    @Getter
    @Setter
    private String role;

    @Column(name = "is_active")
    @Access(AccessType.PROPERTY)
    @JsonView(UserView.Min.class)
    @Getter
    @Setter
    private Boolean isActive;

    @Version
    @Column(name = "version", columnDefinition = "TIMESTAMP", precision = 3)
    @Access(AccessType.FIELD)
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDesSerializer.class)
    @Getter
    private LocalDateTime version;
}