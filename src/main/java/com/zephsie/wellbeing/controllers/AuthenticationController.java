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
import com.zephsie.wellbeing.utils.converters.api.IEntityDTOConverter;
import com.zephsie.wellbeing.utils.exceptions.ErrorsToExceptionConverter;
import com.zephsie.wellbeing.utils.exceptions.InvalidCredentialException;
import com.zephsie.wellbeing.utils.exceptions.ValidationException;
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

    private final IEntityDTOConverter<User, UserDTO> userDTOConverter;

    private final IEntityDTOConverter<VerificationToken, VerificationDTO> verificationDTOConverter;

    private final IEntityDTOConverter<User, LoginDTO> loginDTOConverter;

    private final ApplicationEventPublisher eventPublisher;

    private final ErrorsToExceptionConverter errorsToExceptionConverter;

    @Autowired
    public AuthenticationController(AuthenticationManager authenticationManager, UserDetailsServiceImpl userDetailsService, IAuthenticationService authenticationService, JwtUtil jwtUtil, IEntityDTOConverter<User, UserDTO> userDTOConverter, IEntityDTOConverter<VerificationToken, VerificationDTO> verificationDTOConverter, IEntityDTOConverter<User, LoginDTO> loginDTOConverter, ApplicationEventPublisher eventPublisher, ErrorsToExceptionConverter errorsToExceptionConverter) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.authenticationService = authenticationService;
        this.jwtUtil = jwtUtil;
        this.userDTOConverter = userDTOConverter;
        this.verificationDTOConverter = verificationDTOConverter;
        this.loginDTOConverter = loginDTOConverter;
        this.eventPublisher = eventPublisher;
        this.errorsToExceptionConverter = errorsToExceptionConverter;
    }

    @PostMapping(value = "/login", consumes = "application/json", produces = "application/json")
    public ResponseEntity<Map<String, String>> login(@RequestBody @Valid LoginDTO loginDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw errorsToExceptionConverter.mapErrorsToException(bindingResult, ValidationException.class);
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
    public ResponseEntity<Map<String, String>> register(@RequestBody @Valid UserDTO personDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw errorsToExceptionConverter.mapErrorsToException(bindingResult, ValidationException.class);
        }

        VerificationToken verificationToken = authenticationService.register(userDTOConverter.convertToEntity(personDTO));

        eventPublisher.publishEvent(new OnRegistrationCompleteEvent(verificationToken));

        return ResponseEntity.ok(Map.of("message", "Registration successful. Please check your email for verification link."));
    }

    @PostMapping(value = "/verification", consumes = "application/json", produces = "application/json")
    public ResponseEntity<Map<String, String>> verify(@RequestBody @Valid VerificationDTO verificationDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw errorsToExceptionConverter.mapErrorsToException(bindingResult, ValidationException.class);
        }

        authenticationService.verifyUser(verificationDTOConverter.convertToEntity(verificationDTO));

        UserDetails userDetails = userDetailsService.loadUserByUsername(verificationDTO.getEmail());

        String token = jwtUtil.generateToken(userDetails);

        return ResponseEntity.ok(Map.of("token", token));
    }

    @PutMapping(value = "/token", consumes = "application/json", produces = "application/json")
    public ResponseEntity<Map<String, String>> refresh(@RequestBody @Valid LoginDTO loginDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw errorsToExceptionConverter.mapErrorsToException(bindingResult, ValidationException.class);
        }

        VerificationToken verificationToken = authenticationService.refreshVerificationToken(loginDTOConverter.convertToEntity(loginDTO));

        eventPublisher.publishEvent(new OnRegistrationCompleteEvent(verificationToken));

        return ResponseEntity.ok(Map.of("message", "Registration successful. Please check your email for verification link."));
    }
}