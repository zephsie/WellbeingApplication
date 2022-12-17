package com.zephsie.wellbeing.controllers;

import com.fasterxml.jackson.annotation.JsonView;
import com.zephsie.wellbeing.security.UserDetailsImp;
import com.zephsie.wellbeing.dtos.UserDTO;
import com.zephsie.wellbeing.models.entity.User;
import com.zephsie.wellbeing.services.api.IUserService;
import com.zephsie.wellbeing.utils.converters.UnixTimeToLocalDateTimeConverter;
import com.zephsie.wellbeing.utils.converters.api.IEntityDTOConverter;
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

import java.util.Optional;
import java.util.StringJoiner;
import java.util.UUID;

@RestController
@RequestMapping("/api/user")
public class UserController {
    private final IUserService userService;

    private final UnixTimeToLocalDateTimeConverter unixTimeToLocalDateTimeConverter;

    private final IEntityDTOConverter<User, UserDTO> userDTOConverter;

    @Autowired
    public UserController(IUserService userService, UnixTimeToLocalDateTimeConverter unixTimeToLocalDateTimeConverter, IEntityDTOConverter<User, UserDTO> userDTOConverter) {
        this.userService = userService;
        this.unixTimeToLocalDateTimeConverter = unixTimeToLocalDateTimeConverter;
        this.userDTOConverter = userDTOConverter;
    }

    @GetMapping("/{id}")
    @JsonView(UserView.Min.class)
    public ResponseEntity<User> read(@PathVariable("id") UUID id,
                                     @AuthenticationPrincipal UserDetailsImp userDetailsImp) {

        if (!userDetailsImp.getUser().getId().equals(id)) {
            throw new ValidationException("You can only view your own profile");
        }

        Optional<User> user = userService.read(id);

        if (user.isEmpty()) {
            throw new NotFoundException("User with id " + id + " not found");
        }

        return ResponseEntity.ok(user.get());
    }

    @PutMapping("/{id}/version/{version}")
    @JsonView(UserView.Min.class)
    public ResponseEntity<User> update(@PathVariable("id") UUID id,
                                       @PathVariable("version") long version,
                                       @RequestBody @Valid UserDTO userDTO,
                                       BindingResult bindingResult,
                                       @AuthenticationPrincipal UserDetailsImp userDetailsImp) {

        if (!userDetailsImp.getUser().getId().equals(id)) {
            throw new ValidationException("You can only update your own account");
        }

        if (bindingResult.hasErrors()) {
            StringJoiner stringJoiner = new StringJoiner(", ");
            bindingResult.getAllErrors().forEach(error -> stringJoiner.add(error.getDefaultMessage()));
            throw new ValidationException(stringJoiner.toString());
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
            throw new ValidationException("You can only delete your own account");
        }

        userService.delete(id, unixTimeToLocalDateTimeConverter.convert(version));

        SecurityContextHolder.clearContext();

        return ResponseEntity.ok().build();
    }
}