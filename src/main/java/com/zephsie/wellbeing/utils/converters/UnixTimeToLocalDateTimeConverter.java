package com.zephsie.wellbeing.utils.converters;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.TimeZone;

@Component
public class UnixTimeToLocalDateTimeConverter {
    public LocalDateTime convert(long unixTime) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(unixTime), TimeZone.getDefault().toZoneId());
    }
}