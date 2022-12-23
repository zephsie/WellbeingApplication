package com.zephsie.wellbeing.utils.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomResponseSender {

    private final ObjectMapper objectMapper;

    @Autowired
    public CustomResponseSender(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }


    public void send(HttpServletResponse response, int code, Object o) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(code);
        response.getWriter().write(objectMapper.writeValueAsString(o));
    }
}