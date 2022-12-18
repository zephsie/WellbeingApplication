package com.zephsie.wellbeing.controllers;

import com.fasterxml.jackson.annotation.JsonView;
import com.zephsie.wellbeing.models.entity.User;
import com.zephsie.wellbeing.services.api.IUserService;
import com.zephsie.wellbeing.utils.converters.UnixTimeToLocalDateTimeConverter;
import com.zephsie.wellbeing.utils.exceptions.IllegalPaginationValuesException;
import com.zephsie.wellbeing.utils.exceptions.NotFoundException;
import com.zephsie.wellbeing.utils.json.converters.PageJSONConverter;
import com.zephsie.wellbeing.utils.json.custom.CustomPage;
import com.zephsie.wellbeing.utils.views.UserView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final IUserService userService;

    private final UnixTimeToLocalDateTimeConverter unixTimeToLocalDateTimeConverter;

    private final PageJSONConverter pageJSONConverter;

    @Autowired
    public AdminController(IUserService userService, UnixTimeToLocalDateTimeConverter unixTimeToLocalDateTimeConverter, PageJSONConverter pageJSONConverter) {
        this.userService = userService;
        this.unixTimeToLocalDateTimeConverter = unixTimeToLocalDateTimeConverter;
        this.pageJSONConverter = pageJSONConverter;
    }

    @PutMapping("/{id}/role/{role}/version/{version}")
    public ResponseEntity<User> updateRole(@PathVariable("id") UUID id,
                                           @PathVariable("role") String role,
                                           @PathVariable("version") long version) {

        return ResponseEntity.ok(userService.updateRole(id, role, unixTimeToLocalDateTimeConverter.convert(version)));
    }

    @GetMapping("/{id}")
    @JsonView(UserView.Min.class)
    public ResponseEntity<User> read(@PathVariable("id") UUID id) {

        return userService.read(id).map(ResponseEntity::ok).orElseThrow(() -> new NotFoundException("User with id " + id + " not found"));
    }

    @GetMapping
    public ResponseEntity<CustomPage> read(@RequestParam int page,
                                           @RequestParam int size) {

        if (page < 0 || size <= 0) {
            throw new IllegalPaginationValuesException("Pagination values are not correct");
        }

        return ResponseEntity.ok(pageJSONConverter.convertPage(userService.read(page, size)));
    }
}