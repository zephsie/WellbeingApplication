package com.zephsie.wellbeing.services.api;

import com.zephsie.wellbeing.dtos.JournalDTO;
import com.zephsie.wellbeing.models.entity.Journal;
import com.zephsie.wellbeing.models.entity.User;
import org.springframework.data.domain.Page;

import java.util.Optional;
import java.util.UUID;

public interface IJournalService {
    Journal createWithProduct(JournalDTO journalDTO, User user);

    Journal createWithRecipe(JournalDTO journalDTO, User user);

    Optional<Journal> read(UUID id);

    Page<Journal> read(int page, int size);
}