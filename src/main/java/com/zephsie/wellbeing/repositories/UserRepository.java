package com.zephsie.wellbeing.repositories;

import com.zephsie.wellbeing.models.entity.Role;
import com.zephsie.wellbeing.models.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);

    @NonNull
    Page<User> findAll(@NonNull Pageable pageable);

    boolean existsByEmail(String email);

    long countByRole(Role role);
}