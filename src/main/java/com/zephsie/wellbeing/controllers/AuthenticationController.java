package com.zephsie.wellbeing.controllers;

import com.zephsie.wellbeing.dtos.LoginDTO;
import com.zephsie.wellbeing.dtos.UserDTO;
import com.zephsie.wellbeing.dtos.VerificationDTO;
import com.zephsie.wellbeing.events.OnRegistrationCompleteEvent;
import com.zephsie.wellbeing.models.entity.User;
import com.zephsie.wellbeing.models.entity.VerificationToken;
import com.zephsie.wellbeing.security.jwt.JwtUtil;
import com.zephsie.wellbeing.services.api.IAuthenticationService;
import com.zephsie.wellbeing.services.entity.UserDetailsServiceImpl;
import com.zephsie.wellbeing.utils.converters.ErrorsToMapConverter;
import com.zephsie.wellbeing.utils.exceptions.BasicFieldValidationException;
import com.zephsie.wellbeing.utils.exceptions.InvalidCredentialException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;

    private final UserDetailsServiceImpl userDetailsService;

    private final IAuthenticationService authenticationService;

    private final JwtUtil jwtUtil;

    private final ApplicationEventPublisher eventPublisher;

    private final ErrorsToMapConverter errorsToMapConverter;

    @Autowired
    public AuthenticationController(AuthenticationManager authenticationManager,
                                    UserDetailsServiceImpl userDetailsService,
                                    IAuthenticationService authenticationService,
                                    JwtUtil jwtUtil,
                                    ApplicationEventPublisher eventPublisher,
                                    ErrorsToMapConverter errorsToMapConverter) {

        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.authenticationService = authenticationService;
        this.jwtUtil = jwtUtil;
        this.eventPublisher = eventPublisher;
        this.errorsToMapConverter = errorsToMapConverter;
    }

    @PostMapping(value = "/login", consumes = "application/json", produces = "application/json")
    public ResponseEntity<Map<String, String>> login(@RequestBody @Valid LoginDTO loginDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new BasicFieldValidationException(errorsToMapConverter.map(bindingResult));
        }

        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginDTO.getEmail(), loginDTO.getPassword()));
        } catch (DisabledException e) {
            throw new InvalidCredentialException("User is disabled");
        } catch (AuthenticationException e) {
            throw new InvalidCredentialException("Invalid credentials");
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(loginDTO.getEmail());

        String token = jwtUtil.generateToken(userDetails);
        return ResponseEntity.ok(Map.of("token", token));
    }

    @PostMapping(value = "/registration", consumes = "application/json", produces = "application/json")
    public ResponseEntity<Map<String, String>> register(@RequestBody @Valid UserDTO personDTO,
                                                        BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            throw new BasicFieldValidationException(errorsToMapConverter.map(bindingResult));
        }

        VerificationToken verificationToken = authenticationService.register(personDTO);

        eventPublisher.publishEvent(new OnRegistrationCompleteEvent(verificationToken));

        return ResponseEntity.ok(Map.of("message", "Registration successful. Please check your email for verification link."));
    }

    @PostMapping(value = "/verification", consumes = "application/json", produces = "application/json")
    public ResponseEntity<Map<String, String>> verify(@RequestBody @Valid VerificationDTO verificationDTO,
                                                      BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            throw new BasicFieldValidationException(errorsToMapConverter.map(bindingResult));
        }

        User user = authenticationService.verifyUser(verificationDTO);

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());

        String token = jwtUtil.generateToken(userDetails);

        return ResponseEntity.ok(Map.of("token", token));
    }

    @PutMapping(value = "/token", consumes = "application/json", produces = "application/json")
    public ResponseEntity<Map<String, String>> refresh(@RequestBody @Valid LoginDTO loginDTO,
                                                       BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            throw new BasicFieldValidationException(errorsToMapConverter.map(bindingResult));
        }

        VerificationToken verificationToken = authenticationService.refreshVerificationToken(loginDTO);

        eventPublisher.publishEvent(new OnRegistrationCompleteEvent(verificationToken));

        return ResponseEntity.ok(Map.of("message", "Registration successful. Please check your email for verification link."));
    }
}