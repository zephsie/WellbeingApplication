package com.zephsie.wellbeing.models.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.zephsie.wellbeing.models.api.IBaseEntity;
import com.zephsie.wellbeing.utils.serializers.CustomLocalDateTimeDesSerializer;
import com.zephsie.wellbeing.utils.serializers.CustomLocalDateTimeSerializer;
import com.zephsie.wellbeing.utils.views.EntityView;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "product", schema = "structure")
@DynamicUpdate
@NoArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@JsonView(EntityView.class)
public class Product implements IBaseEntity<UUID> {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    @Access(AccessType.PROPERTY)
    @Getter
    @Setter
    @JsonView(EntityView.Base.class)
    private UUID id;

    @Column(name = "title", nullable = false)
    @Access(AccessType.PROPERTY)
    @Getter
    @Setter
    @JsonView(EntityView.Base.class)
    private String title;

    @Column(name = "weight", nullable = false)
    @Access(AccessType.PROPERTY)
    @Getter
    @Setter
    @JsonView(EntityView.Base.class)
    private Integer weight;

    @Column(name = "calories", nullable = false)
    @Access(AccessType.PROPERTY)
    @Getter
    @Setter
    @JsonView(EntityView.Base.class)
    private Integer calories;

    @Column(name = "proteins", nullable = false)
    @Access(AccessType.PROPERTY)
    @Getter
    @Setter
    @JsonView(EntityView.Base.class)
    private Double proteins;

    @Column(name = "fats", nullable = false)
    @Access(AccessType.PROPERTY)
    @Getter
    @Setter
    @JsonView(EntityView.Base.class)
    private Double fats;

    @Column(name = "carbohydrates", nullable = false)
    @Access(AccessType.PROPERTY)
    @Getter
    @Setter
    @JsonView(EntityView.Base.class)
    private Double carbohydrates;

    @OneToOne(targetEntity = User.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", updatable = false)
    @Access(AccessType.PROPERTY)
    @Getter
    @Setter
    @JsonView(EntityView.Full.class)
    private User user;

    @Version
    @Column(name = "version", columnDefinition = "TIMESTAMP", precision = 3)
    @Access(AccessType.FIELD)
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDesSerializer.class)
    @Getter
    @JsonView(EntityView.System.class)
    private LocalDateTime version;

    @Column(name = "create_date", columnDefinition = "TIMESTAMP", precision = 3)
    @Access(AccessType.FIELD)
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDesSerializer.class)
    @CreationTimestamp
    @Getter
    @JsonView(EntityView.System.class)
    private LocalDateTime createDate;
}
