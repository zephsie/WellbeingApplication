package com.zephsie.wellbeing.services.api;

import com.zephsie.wellbeing.dtos.UserDTO;
import com.zephsie.wellbeing.models.entity.Role;
import com.zephsie.wellbeing.models.entity.User;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface IUserService {
    Optional<User> read(UUID id);

    Page<User> read(int page, int size);

    User update(UUID id, UserDTO userDTO, LocalDateTime version);

    User updateRole(UUID id, Role role, LocalDateTime version);

    void delete(UUID id, LocalDateTime version);
}