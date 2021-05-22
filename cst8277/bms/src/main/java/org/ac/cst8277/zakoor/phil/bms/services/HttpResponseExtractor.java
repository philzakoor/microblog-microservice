package org.ac.cst8277.zakoor.phil.bms.services;

import java.util.HashMap;
import com.fasterxml.jackson.databind.ObjectMapper;

public class HttpResponseExtractor {
    public static <T> T extractDataFromHttpClientResponse(Object data, Class<T> clazz) {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.convertValue(((HashMap<String, T>) data).get("data"), clazz);
    }
}
