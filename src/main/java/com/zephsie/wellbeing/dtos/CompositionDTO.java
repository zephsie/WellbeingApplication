package com.zephsie.wellbeing.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CompositionDTO {
    private MinProductDTO product;

    private Integer weight;
}