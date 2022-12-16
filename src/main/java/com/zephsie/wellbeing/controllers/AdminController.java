package com.zephsie.wellbeing.controllers;

import com.fasterxml.jackson.annotation.JsonView;
import com.zephsie.wellbeing.models.entity.User;
import com.zephsie.wellbeing.services.api.IUserService;
import com.zephsie.wellbeing.utils.converters.UnixTimeToLocalDateTimeConverter;
import com.zephsie.wellbeing.utils.exceptions.IllegalPaginationValuesException;
import com.zephsie.wellbeing.utils.exceptions.NotFoundException;
import com.zephsie.wellbeing.utils.views.UserView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

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

    @PutMapping("/{id}/role/{role}/version/{version}")
    public ResponseEntity<User> updateRole(@PathVariable("id") long id,
                                           @PathVariable("role") String role,
                                           @PathVariable("version") long version) {

        return ResponseEntity.ok(userService.updateRole(id, role, unixTimeToLocalDateTimeConverter.convert(version)));
    }

    @GetMapping("/{id}")
    @JsonView(UserView.Min.class)
    public ResponseEntity<User> read(@PathVariable("id") long id) {

        Optional<User> user = userService.read(id);

        if (user.isEmpty()) {
            throw new NotFoundException("User with id " + id + " not found");
        }

        return ResponseEntity.ok(user.get());
    }

    @GetMapping
    @JsonView(UserView.Min.class)
    public ResponseEntity<Iterable<User>> read(@RequestParam(defaultValue = "0") int page,
                                               @RequestParam(defaultValue = "25") int size) {

        if (page < 0 || size < 0) {
            throw new IllegalPaginationValuesException("Pagination values are not correct");
        }

        return ResponseEntity.ok(userService.read(page, size));
    }
}