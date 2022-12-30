package com.zephsie.wellbeing.utils.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.springframework.boot.jackson.JsonComponent;
import org.springframework.data.domain.Page;

import java.io.IOException;

@JsonComponent
@SuppressWarnings("rawtypes")
public class PageSerializer extends StdSerializer<Page> {
    public PageSerializer() {
        super(Page.class);
    }

    @Override
    public void serialize(Page page, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeNumberField("number", page.getNumber());
        jsonGenerator.writeNumberField("size", page.getSize());
        jsonGenerator.writeNumberField("totalPages", page.getTotalPages());
        jsonGenerator.writeNumberField("totalElements", page.getTotalElements());
        jsonGenerator.writeBooleanField("first", page.isFirst());
        jsonGenerator.writeNumberField("numberOfElements", page.getNumberOfElements());
        jsonGenerator.writeBooleanField("last", page.isLast());
        jsonGenerator.writeFieldName("content");
        serializerProvider.defaultSerializeValue(page.getContent(), jsonGenerator);
        jsonGenerator.writeEndObject();
    }
}