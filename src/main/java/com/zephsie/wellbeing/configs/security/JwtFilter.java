package com.zephsie.wellbeing.configs.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zephsie.wellbeing.services.entity.UserDetailsServiceImpl;
import com.zephsie.wellbeing.utils.responses.ErrorResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    private final UserDetailsServiceImpl userDetailsService;

    private final ObjectMapper objectMapper;

    @Autowired
    public JwtFilter(JwtUtil jwtUtil, UserDetailsServiceImpl userDetailsService, ObjectMapper objectMapper) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
        this.objectMapper = objectMapper;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String jwt = authHeader.substring(7);
        String username;

        try {
            username = jwtUtil.extractUsername(jwt);
        } catch (Exception e) {
            sendError(response, "Token is malformed");
            return;
        }

        if (username == null || SecurityContextHolder.getContext().getAuthentication() != null) {
            sendError(response, "Username is missing");
            return;
        }

        UserDetails userDetails;

        try {
            userDetails = userDetailsService.loadUserByUsername(username);
        } catch (Exception e) {
            sendError(response, "User with " + username + " does not exist");
            return;
        }

        if (!jwtUtil.validateToken(jwt, userDetails)) {
            sendError(response, "Token is invalid");
        }

        if (!userDetails.isEnabled()) {
            sendError(response, "User is disabled");
            return;
        }

        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
        filterChain.doFilter(request, response);
    }

    private void sendError(HttpServletResponse response, String message) throws IOException {
        ErrorResponse errorResponse = new ErrorResponse(message, System.currentTimeMillis());
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}