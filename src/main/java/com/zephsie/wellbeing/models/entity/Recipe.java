package com.zephsie.wellbeing.models.entity;

import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.zephsie.wellbeing.models.api.IImmutableEntity;
import com.zephsie.wellbeing.utils.serializers.CustomLocalDateTimeDesSerializer;
import com.zephsie.wellbeing.utils.serializers.CustomLocalDateTimeSerializer;
import com.zephsie.wellbeing.utils.views.EntityView;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "recipe", schema = "structure")
@NoArgsConstructor
@JsonView(EntityView.class)
@NamedEntityGraph(
        name = "recipeWithCompositionsAndProducts",
        attributeNodes =
                {
                        @NamedAttributeNode("composition"),
                        @NamedAttributeNode(value = "composition", subgraph = "compositionWithProduct"),
                },
        subgraphs = @NamedSubgraph
                (
                        name = "compositionWithProduct",
                        attributeNodes = @NamedAttributeNode("product")
                )
)
public class Recipe implements IImmutableEntity<UUID> {
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

    @OneToMany(mappedBy = "recipe", cascade = {CascadeType.PERSIST})
    @Getter
    @JsonView(EntityView.WithMappings.class)
    private List<Composition> composition = new ArrayList<>();

    @OneToOne(targetEntity = User.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", updatable = false)
    @Access(AccessType.PROPERTY)
    @Getter
    @Setter
    @JsonView(EntityView.Full.class)
    private User user;

    @Column(name = "dt_create", columnDefinition = "TIMESTAMP", precision = 3)
    @Access(AccessType.FIELD)
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDesSerializer.class)
    @CreationTimestamp
    @Getter
    @JsonView(EntityView.System.class)
    private LocalDateTime dtCreate;

    public void addComposition(Composition composition) {
        this.composition.add(composition);
        composition.setRecipe(this);
    }

    public void removeComposition(Composition composition) {
        this.composition.remove(composition);
        composition.setRecipe(null);
    }

    public void clearComposition() {
        this.composition.clear();
    }
}