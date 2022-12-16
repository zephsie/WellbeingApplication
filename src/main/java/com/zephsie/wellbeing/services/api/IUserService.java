package com.zephsie.wellbeing.services.api;

import com.zephsie.wellbeing.models.entity.User;

import java.time.LocalDateTime;
import java.util.Optional;

public interface IUserService {

    void update(Long id, User user, LocalDateTime version);

    void delete(Long id, LocalDateTime version);

    Optional<User> read(Long id);

    Iterable<User> read(int page, int size);

    User updateRole(Long id, String role, LocalDateTime version);
}