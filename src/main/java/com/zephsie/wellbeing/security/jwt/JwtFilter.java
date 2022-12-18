package com.zephsie.wellbeing.security.jwt;

import com.zephsie.wellbeing.services.entity.UserDetailsServiceImpl;
import com.zephsie.wellbeing.utils.http.CustomResponseSender;
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

    private final Clock clock;

    private final CustomResponseSender customResponseSender;

    @Autowired
    public JwtFilter(JwtUtil jwtUtil, UserDetailsServiceImpl userDetailsService, Clock clock, CustomResponseSender customResponseSender) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
        this.clock = clock;
        this.customResponseSender = customResponseSender;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        if (request.getRequestURI().startsWith("/api/auth/")) {
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            ErrorResponse errorResponse = new ErrorResponse("Authorization header is missing or invalid", clock.millis());
            customResponseSender.send(response, HttpServletResponse.SC_UNAUTHORIZED, errorResponse);
            return;
        }

        String jwt = authHeader.substring(7);
        String username;

        try {
            username = jwtUtil.extractUsername(jwt);
        } catch (Exception e) {
            ErrorResponse errorResponse = new ErrorResponse("Token is invalid", clock.millis());
            customResponseSender.send(response, HttpServletResponse.SC_FORBIDDEN, errorResponse);
            return;
        }

        if (username == null) {
            ErrorResponse errorResponse = new ErrorResponse("Could not extract username from token", clock.millis());
            customResponseSender.send(response, HttpServletResponse.SC_FORBIDDEN, errorResponse);
            return;
        }

        UserDetails userDetails;

        try {
            userDetails = userDetailsService.loadUserByUsername(username);
        } catch (UsernameNotFoundException e) {
            ErrorResponse errorResponse = new ErrorResponse("User with " + username + " does not exist", clock.millis());
            customResponseSender.send(response, HttpServletResponse.SC_FORBIDDEN, errorResponse);
            return;
        }

        if (!jwtUtil.validateToken(jwt, userDetails)) {
            ErrorResponse errorResponse = new ErrorResponse("Token is invalid", clock.millis());
            customResponseSender.send(response, HttpServletResponse.SC_FORBIDDEN, errorResponse);
            return;
        }

        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
        filterChain.doFilter(request, response);
    }
}