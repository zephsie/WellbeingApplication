package com.zephsie.wellbeing.controllers;

import com.fasterxml.jackson.annotation.JsonView;
import com.zephsie.wellbeing.dtos.JournalDTO;
import com.zephsie.wellbeing.models.entity.Journal;
import com.zephsie.wellbeing.security.UserDetailsImp;
import com.zephsie.wellbeing.services.api.IJournalService;
import com.zephsie.wellbeing.utils.exceptions.BasicFieldValidationException;
import com.zephsie.wellbeing.utils.exceptions.IllegalPaginationValuesException;
import com.zephsie.wellbeing.utils.exceptions.NotFoundException;
import com.zephsie.wellbeing.utils.groups.BasicJournalFieldsSequence;
import com.zephsie.wellbeing.utils.groups.TotalJournalSequence;
import com.zephsie.wellbeing.utils.views.EntityView;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/journal")
public class JournalController {

    private final IJournalService journalService;

    private final Validator validator;

    @Autowired
    public JournalController(IJournalService journalService, Validator validator) {
        this.journalService = journalService;
        this.validator = validator;
    }

    @GetMapping(value = "/{id}", produces = "application/json")
    @JsonView(EntityView.WithMappings.class)
    public ResponseEntity<Journal> read(@PathVariable("id") UUID id,
                                        @AuthenticationPrincipal UserDetailsImp userDetails) {

        return journalService.read(id, userDetails.getUser())
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new NotFoundException("Journal with id " + id + " not found"));
    }

    @PostMapping(consumes = "application/json", produces = "application/json")
    @JsonView(EntityView.WithMappings.class)
    public ResponseEntity<Journal> create(@RequestBody JournalDTO journalDTO,
                                          @AuthenticationPrincipal UserDetailsImp userDetails) {

        Set<ConstraintViolation<JournalDTO>> set = validator
                .validate(journalDTO, BasicJournalFieldsSequence.class, TotalJournalSequence.class);

        if (!set.isEmpty()) {
            throw new BasicFieldValidationException(set.stream()
                    .collect(HashMap::new, (m, v) -> m.put(v.getPropertyPath().toString(), v.getMessage()), HashMap::putAll));
        }

        return journalDTO.getRecipe() == null ?
                ResponseEntity.status(HttpStatus.CREATED).body(journalService.createWithProduct(journalDTO, userDetails.getUser())) :
                ResponseEntity.status(HttpStatus.CREATED).body(journalService.createWithRecipe(journalDTO, userDetails.getUser()));
    }

    @GetMapping(produces = "application/json")
    @JsonView(EntityView.WithMappings.class)
    public ResponseEntity<Page<Journal>> read(@RequestParam(value = "page", defaultValue = "0") int page,
                                              @RequestParam(value = "size", defaultValue = "10") int size,
                                              @AuthenticationPrincipal UserDetailsImp userDetails) {

        if (page < 0 || size <= 0) {
            throw new IllegalPaginationValuesException("Pagination values are not correct");
        }

        return ResponseEntity.ok(journalService.read(page, size, userDetails.getUser()));
    }
}