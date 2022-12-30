package com.zephsie.wellbeing.repositories;

import com.zephsie.wellbeing.models.entity.Journal;
import com.zephsie.wellbeing.models.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface JournalRepository extends JpaRepository<Journal, UUID> {
    @NonNull
    Page<Journal> findAllByUser(@NonNull Pageable pageable, @NonNull User user);

    @NonNull
    Optional<Journal> findByIdAndUser(@NonNull UUID id, @NonNull User user);
}