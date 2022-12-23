package com.zephsie.wellbeing.controllers;

import com.fasterxml.jackson.annotation.JsonView;
import com.zephsie.wellbeing.models.entity.Role;
import com.zephsie.wellbeing.models.entity.User;
import com.zephsie.wellbeing.services.api.IUserService;
import com.zephsie.wellbeing.utils.converters.UnixTimeToLocalDateTimeConverter;
import com.zephsie.wellbeing.utils.exceptions.IllegalPaginationValuesException;
import com.zephsie.wellbeing.utils.exceptions.NotFoundException;
import com.zephsie.wellbeing.utils.views.UserView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final IUserService userService;

    private final UnixTimeToLocalDateTimeConverter unixTimeToLocalDateTimeConverter;

    @Autowired
    public AdminController(IUserService userService, UnixTimeToLocalDateTimeConverter unixTimeToLocalDateTimeConverter) {
        this.userService = userService;
        this.unixTimeToLocalDateTimeConverter = unixTimeToLocalDateTimeConverter;
    }

    @PutMapping(value = "/{id}/role/{role}/version/{version}", produces = "application/json")
    @JsonView(UserView.System.class)
    public ResponseEntity<User> updateRole(@PathVariable("id") UUID id,
                                           @PathVariable("role") Role role,
                                           @PathVariable("version") long version) {

        return ResponseEntity.ok(userService.updateRole(id, role, unixTimeToLocalDateTimeConverter.convert(version)));
    }

    @GetMapping(value = "/{id}", produces = "application/json")
    @JsonView(UserView.System.class)
    public ResponseEntity<User> read(@PathVariable("id") UUID id) {

        return userService.read(id).map(ResponseEntity::ok)
                .orElseThrow(() -> new NotFoundException("User with id " + id + " not found"));
    }

    @GetMapping(produces = "application/json")
    @JsonView(UserView.System.class)
    public ResponseEntity<Page<User>> read(@RequestParam(value = "page", defaultValue = "0") int page,
                                           @RequestParam(value = "size", defaultValue = "10") int size) {

        if (page < 0 || size <= 0) {
            throw new IllegalPaginationValuesException("Pagination values are not correct");
        }

        return ResponseEntity.ok(userService.read(page, size));
    }
}