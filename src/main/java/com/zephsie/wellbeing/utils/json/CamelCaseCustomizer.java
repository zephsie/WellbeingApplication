package com.zephsie.wellbeing.utils.json;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.boot.jackson.JsonComponent;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

@JsonComponent
public class CamelCaseCustomizer implements Jackson2ObjectMapperBuilderCustomizer {
    @Override
    public void customize(Jackson2ObjectMapperBuilder builder) {
        builder.propertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
    }
}