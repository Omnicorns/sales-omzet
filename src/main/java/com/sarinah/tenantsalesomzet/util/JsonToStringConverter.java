package com.sarinah.tenantsalesomzet.util;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.json.JsonMapper;

public class JsonToStringConverter {
    private static final JsonMapper MAPPER = JsonMapper.builder().build();

    private JsonToStringConverter() {}

    public static String convertJsonToString(Object data) throws JacksonException {
        return MAPPER.writeValueAsString(data);
    }

    public static Object convertJsonToObject(Object data) {
        return MAPPER.convertValue(data, Object.class);
    }
}
