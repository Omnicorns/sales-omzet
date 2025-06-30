package com.sarinah.tenantsalesomzet.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonToStringConverter {
    private JsonToStringConverter() {}

    public static String convertJsonToString(Object data) throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(data);
    }

    public static Object convertJsonToObject(Object data) {
        return new ObjectMapper().convertValue(data, Object.class);
    }
}
