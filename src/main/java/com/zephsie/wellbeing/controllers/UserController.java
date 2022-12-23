package com.zephsie.wellbeing.controllers;

import com.fasterxml.jackson.annotation.JsonView;
import com.zephsie.wellbeing.dtos.UserDTO;
import com.zephsie.wellbeing.models.entity.User;
import com.zephsie.wellbeing.security.UserDetailsImp;
import com.zephsie.wellbeing.services.api.IUserService;
import com.zephsie.wellbeing.utils.converters.ErrorsToMapConverter;
import com.zephsie.wellbeing.utils.converters.UnixTimeToLocalDateTimeConverter;
import com.zephsie.wellbeing.utils.exceptions.BasicFieldValidationException;
import com.zephsie.wellbeing.utils.views.UserView;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {
    private final IUserService userService;

    private final UnixTimeToLocalDateTimeConverter unixTimeToLocalDateTimeConverter;

    private final ErrorsToMapConverter errorsToMapConverter;

    @Autowired
    public UserController(IUserService userService,
                          UnixTimeToLocalDateTimeConverter unixTimeToLocalDateTimeConverter,
                          ErrorsToMapConverter errorsToMapConverter) {

        this.userService = userService;
        this.unixTimeToLocalDateTimeConverter = unixTimeToLocalDateTimeConverter;
        this.errorsToMapConverter = errorsToMapConverter;
    }

    @GetMapping(value = "/me", produces = "application/json")
    @JsonView(UserView.System.class)
    public ResponseEntity<User> read(@AuthenticationPrincipal UserDetailsImp userDetailsImp) {
        return ResponseEntity.ok(userDetailsImp.getUser());
    }

    @PutMapping(value = "/me/version/{version}", consumes = "application/json", produces = "application/json")
    @JsonView(UserView.System.class)
    public ResponseEntity<User> update(@PathVariable("version") long version,
                                       @RequestBody @Valid UserDTO userDTO,
                                       BindingResult bindingResult,
                                       @AuthenticationPrincipal UserDetailsImp userDetailsImp) {

        if (bindingResult.hasErrors()) {
            throw new BasicFieldValidationException(errorsToMapConverter.map(bindingResult));
        }

        return ResponseEntity.ok(userService.update(userDetailsImp.getUser().getId(), userDTO,
                unixTimeToLocalDateTimeConverter.convert(version)));
    }

    @DeleteMapping(value = "/me/version/{version}", produces = "application/json")
    public ResponseEntity<Void> delete(@PathVariable("version") long version,
                                       @AuthenticationPrincipal UserDetailsImp userDetailsImp) {

        userService.delete(userDetailsImp.getUser().getId(), unixTimeToLocalDateTimeConverter.convert(version));
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok().build();
    }
}