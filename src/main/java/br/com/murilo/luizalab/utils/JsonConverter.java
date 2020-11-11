package br.com.murilo.luizalab.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;

import java.io.IOException;

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
}
