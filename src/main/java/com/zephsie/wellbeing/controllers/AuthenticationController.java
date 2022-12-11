package com.zephsie.wellbeing.controllers;

import com.zephsie.wellbeing.configs.security.JwtUtil;
import com.zephsie.wellbeing.dtos.LoginDTO;
import com.zephsie.wellbeing.dtos.UserDTO;
import com.zephsie.wellbeing.dtos.VerificationDTO;
import com.zephsie.wellbeing.models.entity.VerificationToken;
import com.zephsie.wellbeing.services.entity.AuthenticationService;
import com.zephsie.wellbeing.services.entity.EmailVerificationService;
import com.zephsie.wellbeing.services.entity.UserDetailsServiceImpl;
import com.zephsie.wellbeing.utils.converters.UserDTOConverter;
import com.zephsie.wellbeing.utils.exceptions.EmailException;
import com.zephsie.wellbeing.utils.exceptions.ValidationException;
import com.zephsie.wellbeing.utils.responses.InfoResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.StringJoiner;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;

    private final UserDetailsServiceImpl userDetailsService;

    private final AuthenticationService authenticationService;

    private final JwtUtil jwtUtil;

    private final UserDTOConverter userDTOConverter;

    private final EmailVerificationService emailVerificationService;

    @Autowired
    public AuthenticationController(AuthenticationManager authenticationManager, UserDetailsServiceImpl userDetailsService, AuthenticationService authenticationService, JwtUtil jwtUtil, UserDTOConverter userDTOConverter, EmailVerificationService emailVerificationService) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.authenticationService = authenticationService;
        this.jwtUtil = jwtUtil;
        this.userDTOConverter = userDTOConverter;
        this.emailVerificationService = emailVerificationService;
    }

    @PostMapping(value = "/login", consumes = "application/json", produces = "application/json")
    public ResponseEntity<InfoResponse> login(@RequestBody @Valid LoginDTO loginDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            StringJoiner stringJoiner = new StringJoiner(", ");
            bindingResult.getAllErrors().forEach(error -> stringJoiner.add(error.getDefaultMessage()));
            throw new ValidationException(stringJoiner.toString());
        }

        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginDTO.getEmail(), loginDTO.getPassword()));
        } catch (DisabledException e) {
            throw new ValidationException("User is disabled");
        } catch (AuthenticationException e) {
            throw new ValidationException("Invalid credentials");
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(loginDTO.getEmail());

        String token = jwtUtil.generateToken(userDetails);
        return ResponseEntity.ok(new InfoResponse(Map.of("token", token)));
    }

    @PostMapping(value = "/registration", consumes = "application/json", produces = "application/json")
    public ResponseEntity<InfoResponse> register(@RequestBody @Valid UserDTO personDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            StringJoiner joiner = new StringJoiner(", ");
            bindingResult.getAllErrors().forEach(objectError -> joiner.add(objectError.getDefaultMessage()));
            throw new ValidationException(joiner.toString());
        }

        VerificationToken verificationToken = authenticationService.register(userDTOConverter.convertToEntity(personDTO));

        try {
            emailVerificationService.sendVerificationEmail(verificationToken);
        } catch (Exception e) {
            throw new EmailException("Failed to send verification email to " + verificationToken.getUser().getEmail() + ". Please try again later.");
        }

        return ResponseEntity.ok(new InfoResponse(Map.of("message", "Registration successful. Please check your email for verification link.")));
    }

    @PostMapping(value = "/verification", consumes = "application/json", produces = "application/json")
    public ResponseEntity<InfoResponse> verify(@RequestBody @Valid VerificationDTO verificationDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            StringJoiner joiner = new StringJoiner(", ");
            bindingResult.getAllErrors().forEach(objectError -> joiner.add(objectError.getDefaultMessage()));
            throw new ValidationException(joiner.toString());
        }

        authenticationService.verifyUser(verificationDTO);

        UserDetails userDetails = userDetailsService.loadUserByUsername(verificationDTO.getEmail());

        String token = jwtUtil.generateToken(userDetails);

        return ResponseEntity.ok(new InfoResponse(Map.of("token", token)));
    }

    @PutMapping(value = "/token", consumes = "application/json", produces = "application/json")
    public ResponseEntity<InfoResponse> refresh(@RequestBody @Valid UserDTO personDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            StringJoiner joiner = new StringJoiner(", ");
            bindingResult.getAllErrors().forEach(objectError -> joiner.add(objectError.getDefaultMessage()));
            throw new ValidationException(joiner.toString());
        }

        VerificationToken verificationToken = authenticationService.refreshVerificationToken(userDTOConverter.convertToEntity(personDTO));

        try {
            emailVerificationService.sendVerificationEmail(verificationToken);
        } catch (Exception e) {
            throw new EmailException("Failed to send verification email to " + verificationToken.getUser().getEmail() + ". Please try again later.");
        }

        return ResponseEntity.ok(new InfoResponse(Map.of("message", "Registration successful. Please check your email for verification link.")));
    }
}
