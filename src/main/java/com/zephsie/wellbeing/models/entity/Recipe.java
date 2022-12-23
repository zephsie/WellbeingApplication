package com.zephsie.wellbeing.models.entity;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.zephsie.wellbeing.models.api.IBaseEntity;
import com.zephsie.wellbeing.utils.serializers.CustomLocalDateTimeDesSerializer;
import com.zephsie.wellbeing.utils.serializers.CustomLocalDateTimeSerializer;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "recipe", schema = "structure")
@DynamicUpdate
@NoArgsConstructor
public class Recipe implements IBaseEntity<UUID> {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    @Access(AccessType.PROPERTY)
    @Getter
    @Setter
    private UUID id;

    @Column(name = "title", nullable = false)
    @Access(AccessType.PROPERTY)
    @Getter
    @Setter
    private String title;

    @Column(name = "weight", nullable = false)
    @Access(AccessType.PROPERTY)
    @Getter
    @Setter
    private Integer weight;

    @OneToMany(mappedBy = "recipe", cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, fetch = FetchType.EAGER)
    @Getter
    @Setter
    private List<Composition> compositions;

    @OneToOne(targetEntity = User.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", updatable = false)
    @Access(AccessType.PROPERTY)
    @Getter
    @Setter
    private User user;

    @Version
    @Column(name = "version", columnDefinition = "TIMESTAMP", precision = 3)
    @Access(AccessType.FIELD)
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDesSerializer.class)
    @Getter
    private LocalDateTime version;

    @Column(name = "create_date", columnDefinition = "TIMESTAMP", precision = 3)
    @Access(AccessType.FIELD)
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDesSerializer.class)
    @CreationTimestamp
    @Getter
    private LocalDateTime createDate;
}