package org.yaremax.javaweb20240908.service.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class JsonUtil {
    private static JsonUtil instance;
    private final ObjectMapper mapper;

    private JsonUtil() {
        mapper = new ObjectMapper();
    }

    public static synchronized JsonUtil getInstance() {
        if (instance == null) {
            instance = new JsonUtil();
        }
        return instance;
    }

    public <T> T parseRequestBody(HttpServletRequest request, Class<T> clazz) throws IOException {
        return mapper.readValue(request.getInputStream(), clazz);
    }

    public void writeResponse(HttpServletResponse response, int statusCode, Object data) throws IOException {
        response.setContentType("application/json");
        response.setStatus(statusCode);
        mapper.writeValue(response.getOutputStream(), data);
    }
}