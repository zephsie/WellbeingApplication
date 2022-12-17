package com.zephsie.wellbeing.security.jwt;

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
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Clock;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    private final UserDetailsServiceImpl userDetailsService;

    private final ObjectMapper objectMapper;

    private final Clock clock;

    @Autowired
    public JwtFilter(JwtUtil jwtUtil, UserDetailsServiceImpl userDetailsService, ObjectMapper objectMapper, Clock clock) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
        this.objectMapper = objectMapper;
        this.clock = clock;
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
            sendError(response, "Token is invalid", HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        if (username == null) {
            sendError(response, "Could not extract username from token", HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        UserDetails userDetails;

        try {
            userDetails = userDetailsService.loadUserByUsername(username);
        } catch (UsernameNotFoundException e) {
            sendError(response, "User with " + username + " does not exist", HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        if (!jwtUtil.validateToken(jwt, userDetails)) {
            sendError(response, "Token is invalid", HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
        filterChain.doFilter(request, response);
    }

    private void sendError(HttpServletResponse response, String message, int code) throws IOException {
        ErrorResponse errorResponse = new ErrorResponse(message, clock.millis());
        response.setContentType("application/json");
        response.setStatus(code);
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}