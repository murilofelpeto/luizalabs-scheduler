package br.com.murilo.luizalab.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;

import java.io.IOException;
import java.text.SimpleDateFormat;

public class JsonConverter {

    public static <T> Object convert(String json, Class<? extends T> clazz) {

        if (Strings.isNullOrEmpty(json)) {
            return null;
        }

        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(json, clazz);
        } catch (IOException e) {
            throw new IllegalStateException("Não foi possivel serializar o objeto");
        }
    }

    public static String convert(Object obj) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        ObjectMapper mapper = new ObjectMapper();
        mapper.setDateFormat(df);

        try {
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Não foi possivel deserializar o objeto");
        }
    }
}
