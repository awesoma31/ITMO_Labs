package com.cryptoterm.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * MongoDB configuration with custom converters for Java 8 Time API.
 * 
 * This configuration provides converters for OffsetDateTime to properly 
 * serialize/deserialize dates with timezone information in MongoDB.
 */
@Configuration
public class MongoConfig {

    @Bean
    public MongoCustomConversions customConversions() {
        List<Converter<?, ?>> converters = new ArrayList<>();
        converters.add(new OffsetDateTimeToDateConverter());
        converters.add(new DateToOffsetDateTimeConverter());
        return new MongoCustomConversions(converters);
    }

    /**
     * Converter to store OffsetDateTime as Date in MongoDB.
     * Converts OffsetDateTime to UTC before storing.
     */
    static class OffsetDateTimeToDateConverter implements Converter<OffsetDateTime, Date> {
        @Override
        public Date convert(OffsetDateTime source) {
            if (source == null) {
                return null;
            }
            // Convert to UTC and then to Date
            return Date.from(source.toInstant());
        }
    }

    /**
     * Converter to read Date from MongoDB and convert to OffsetDateTime.
     * Always returns dates in UTC timezone.
     */
    static class DateToOffsetDateTimeConverter implements Converter<Date, OffsetDateTime> {
        @Override
        public OffsetDateTime convert(Date source) {
            if (source == null) {
                return null;
            }
            // Convert Date to OffsetDateTime in UTC
            return source.toInstant().atOffset(ZoneOffset.UTC);
        }
    }
}
