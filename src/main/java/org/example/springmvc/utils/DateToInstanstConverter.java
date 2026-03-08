package org.example.springmvc.utils;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Component
public class DateToInstanstConverter implements Converter<String, Instant> {
    
    @Override
    public Instant convert(String source) {
        if (source == null || source.isEmpty()) {
            return null;
        }
        LocalDateTime localDateTime = LocalDateTime.parse(source);
        return localDateTime.atZone(ZoneId.systemDefault()).toInstant();
    }
}