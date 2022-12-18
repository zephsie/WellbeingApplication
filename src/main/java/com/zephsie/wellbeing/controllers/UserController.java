package com.zephsie.wellbeing.controllers;

import com.fasterxml.jackson.annotation.JsonView;
import com.zephsie.wellbeing.dtos.UserDTO;
import com.zephsie.wellbeing.models.entity.User;
import com.zephsie.wellbeing.security.UserDetailsImp;
import com.zephsie.wellbeing.services.api.IUserService;
import com.zephsie.wellbeing.utils.converters.UnixTimeToLocalDateTimeConverter;
import com.zephsie.wellbeing.utils.converters.api.IEntityDTOConverter;
import com.zephsie.wellbeing.utils.exceptions.ErrorsToExceptionConverter;
import com.zephsie.wellbeing.utils.exceptions.InvalidCredentialException;
import com.zephsie.wellbeing.utils.exceptions.NotFoundException;
import com.zephsie.wellbeing.utils.exceptions.ValidationException;
import com.zephsie.wellbeing.utils.views.UserView;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/user")
public class UserController {
    private final IUserService userService;

    private final UnixTimeToLocalDateTimeConverter unixTimeToLocalDateTimeConverter;

    private final IEntityDTOConverter<User, UserDTO> userDTOConverter;

    private final ErrorsToExceptionConverter errorsToExceptionConverter;

    @Autowired
    public UserController(IUserService userService, UnixTimeToLocalDateTimeConverter unixTimeToLocalDateTimeConverter, IEntityDTOConverter<User, UserDTO> userDTOConverter, ErrorsToExceptionConverter errorsToExceptionConverter) {
        this.userService = userService;
        this.unixTimeToLocalDateTimeConverter = unixTimeToLocalDateTimeConverter;
        this.userDTOConverter = userDTOConverter;
        this.errorsToExceptionConverter = errorsToExceptionConverter;
    }

    @GetMapping("/{id}")
    @JsonView(UserView.Min.class)
    public ResponseEntity<User> read(@PathVariable("id") UUID id,
                                     @AuthenticationPrincipal UserDetailsImp userDetailsImp) {

        if (!userDetailsImp.getUser().getId().equals(id)) {
            throw new InvalidCredentialException("You can only view your own profile");
        }

        return userService.read(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new NotFoundException("User with id " + id + " not found"));
    }

    @PutMapping("/{id}/version/{version}")
    @JsonView(UserView.Min.class)
    public ResponseEntity<User> update(@PathVariable("id") UUID id,
                                       @PathVariable("version") long version,
                                       @RequestBody @Valid UserDTO userDTO,
                                       BindingResult bindingResult,
                                       @AuthenticationPrincipal UserDetailsImp userDetailsImp) {

        if (!userDetailsImp.getUser().getId().equals(id)) {
            throw new InvalidCredentialException("You can only update your own account");
        }

        if (bindingResult.hasErrors()) {
            throw errorsToExceptionConverter.mapErrorsToException(bindingResult, ValidationException.class);
        }

        User user = userDTOConverter.convertToEntity(userDTO);

        userService.update(id, user, unixTimeToLocalDateTimeConverter.convert(version));

        return ResponseEntity.ok(user);
    }

    @DeleteMapping("/{id}/version/{version}")
    public ResponseEntity<Void> delete(@PathVariable("id") UUID id,
                                       @PathVariable("version") long version,
                                       @AuthenticationPrincipal UserDetailsImp userDetailsImp) {

        if (!userDetailsImp.getUser().getId().equals(id)) {
            throw new InvalidCredentialException("You can only delete your own account");
        }

        userService.delete(id, unixTimeToLocalDateTimeConverter.convert(version));

        SecurityContextHolder.clearContext();

        return ResponseEntity.ok().build();
    }
}