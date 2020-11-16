package br.com.murilo.luizalab.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.google.common.base.Strings;
import org.springframework.hateoas.mediatype.hal.Jackson2HalModule;

import java.io.IOException;
import java.time.format.DateTimeFormatter;

public class JsonConverter {

    public static <T> Object convert(String json, Class<? extends T> clazz) {

        if (Strings.isNullOrEmpty(json)) {
            return null;
        }

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModules(new JavaTimeModule(), new Jackson2HalModule());
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try {
            return (T) mapper.readValue(json, clazz);
        } catch (IOException e) {
            throw new IllegalStateException("Não foi possivel deserializar o objeto: " + e.getMessage());
        }
    }

    public static String convert(Object obj) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new SimpleModule().addSerializer(new LocalDateTimeSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))));

        try {
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Não foi possivel serializar o objeto: " + e.getMessage());
        }
    }
}
