package com.zephsie.wellbeing.controllers;

import com.fasterxml.jackson.annotation.JsonView;
import com.zephsie.wellbeing.dtos.UserDTO;
import com.zephsie.wellbeing.models.entity.User;
import com.zephsie.wellbeing.security.UserDetailsImp;
import com.zephsie.wellbeing.services.api.IUserService;
import com.zephsie.wellbeing.utils.converters.ErrorsToMapConverter;
import com.zephsie.wellbeing.utils.converters.UnixTimeToLocalDateTimeConverter;
import com.zephsie.wellbeing.utils.converters.api.IEntityDTOConverter;
import com.zephsie.wellbeing.utils.exceptions.BasicFieldValidationException;
import com.zephsie.wellbeing.utils.exceptions.NotFoundException;
import com.zephsie.wellbeing.utils.views.UserView;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
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

    ErrorsToMapConverter errorsToMapConverter;

    @Autowired
    public UserController(IUserService userService, UnixTimeToLocalDateTimeConverter unixTimeToLocalDateTimeConverter,
                          IEntityDTOConverter<User, UserDTO> userDTOConverter, ErrorsToMapConverter errorsToMapConverter) {
        this.userService = userService;
        this.unixTimeToLocalDateTimeConverter = unixTimeToLocalDateTimeConverter;
        this.userDTOConverter = userDTOConverter;
        this.errorsToMapConverter = errorsToMapConverter;
    }

    @GetMapping(value = "/{id}", produces = "application/json")
    @JsonView(UserView.Minimal.class)
    public ResponseEntity<User> read(@PathVariable("id") UUID id,
                                     @AuthenticationPrincipal UserDetailsImp userDetailsImp) {

        if (!userDetailsImp.getUser().getId().equals(id)) {
            throw new AccessDeniedException("You can only view your own profile");
        }

        return userService.read(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new NotFoundException("User with id " + id + " not found"));
    }

    @PutMapping(value = "/{id}/version/{version}", consumes = "application/json", produces = "application/json")
    @JsonView(UserView.Minimal.class)
    public ResponseEntity<User> update(@PathVariable("id") UUID id,
                                       @PathVariable("version") long version,
                                       @RequestBody @Valid UserDTO userDTO,
                                       BindingResult bindingResult,
                                       @AuthenticationPrincipal UserDetailsImp userDetailsImp) {

        if (bindingResult.hasErrors()) {
            throw new BasicFieldValidationException(errorsToMapConverter.map(bindingResult));
        }

        if (!userDetailsImp.getUser().getId().equals(id)) {
            throw new AccessDeniedException("You can only update your own account");
        }

        User user = userDTOConverter.convertToEntity(userDTO);

        userService.update(id, user, unixTimeToLocalDateTimeConverter.convert(version));

        return ResponseEntity.ok(user);
    }

    @DeleteMapping(value = "/{id}/version/{version}", produces = "application/json")
    public ResponseEntity<Void> delete(@PathVariable("id") UUID id,
                                       @PathVariable("version") long version,
                                       @AuthenticationPrincipal UserDetailsImp userDetailsImp) {

        if (!userDetailsImp.getUser().getId().equals(id)) {
            throw new AccessDeniedException("You can only delete your own account");
        }

        userService.delete(id, unixTimeToLocalDateTimeConverter.convert(version));

        SecurityContextHolder.clearContext();

        return ResponseEntity.ok().build();
    }
}