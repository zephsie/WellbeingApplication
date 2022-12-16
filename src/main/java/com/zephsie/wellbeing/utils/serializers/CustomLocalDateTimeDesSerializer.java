package com.zephsie.wellbeing.utils.serializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class CustomLocalDateTimeDesSerializer extends StdDeserializer<LocalDateTime> {

    protected CustomLocalDateTimeDesSerializer() {
        this(null);
    }

    protected CustomLocalDateTimeDesSerializer(Class<LocalDateTime> t) {
        super(t);
    }

    @Override
    public LocalDateTime deserialize(JsonParser jsonparser, DeserializationContext context) throws IOException {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(Long.parseLong(jsonparser.getText())), ZoneId.systemDefault());
    }
}