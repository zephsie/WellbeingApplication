package com.zephsie.wellbeing.services.api;

import com.zephsie.wellbeing.models.entity.User;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface IUserService {

    void update(UUID id, User user, LocalDateTime version);

    void delete(UUID id, LocalDateTime version);

    Optional<User> read(UUID id);

    Page<User> read(int page, int size);

    User updateRole(UUID id, String role, LocalDateTime version);
}