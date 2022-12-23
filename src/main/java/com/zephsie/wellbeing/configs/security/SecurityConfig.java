package com.zephsie.wellbeing.configs.security;

import com.zephsie.wellbeing.models.entity.Role;
import com.zephsie.wellbeing.security.jwt.JwtFilter;
import com.zephsie.wellbeing.utils.http.CustomResponseSender;
import com.zephsie.wellbeing.utils.responses.SingleErrorResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestFilter;

@EnableWebSecurity
@Configuration
public class SecurityConfig {

    private final UserDetailsService userDetailsService;

    private final JwtFilter jwtFilter;

    private final CustomResponseSender customResponseSender;

    @Autowired
    public SecurityConfig(UserDetailsService userDetailsService, JwtFilter jwtFilter, CustomResponseSender customResponseSender) {
        this.userDetailsService = userDetailsService;
        this.jwtFilter = jwtFilter;
        this.customResponseSender = customResponseSender;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeHttpRequests((authorizeHttpRequests) -> authorizeHttpRequests
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/admin/**").hasRole(Role.ROLE_ADMIN.name().substring(5))
                        .anyRequest().authenticated()
                )
                .sessionManagement((sessionManagement) -> sessionManagement
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtFilter, SecurityContextHolderAwareRequestFilter.class)

                .exceptionHandling()
                .authenticationEntryPoint((request, response, authException) -> {
                    customResponseSender.send(response, HttpServletResponse.SC_UNAUTHORIZED,
                            new SingleErrorResponse("error", "Unauthorized"));
                })
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    customResponseSender.send(response, HttpServletResponse.SC_FORBIDDEN,
                            new SingleErrorResponse("error", "Forbidden"));
                });

        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();

        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());

        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}