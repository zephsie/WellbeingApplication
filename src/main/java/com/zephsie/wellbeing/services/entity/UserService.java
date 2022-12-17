package com.zephsie.wellbeing.services.entity;

import com.zephsie.wellbeing.models.entity.User;
import com.zephsie.wellbeing.repositories.UserRepository;
import com.zephsie.wellbeing.services.api.IUserService;
import com.zephsie.wellbeing.utils.exceptions.NotFoundException;
import com.zephsie.wellbeing.utils.exceptions.NotUniqueException;
import com.zephsie.wellbeing.utils.exceptions.WrongVersionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService implements IUserService {
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    @Value("#{'${user.roles}'.split(',')}")
    private List<String> roles;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> read(UUID id) {
        return userRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<User> read(int page, int size) {
        return userRepository.findAll(Pageable.ofSize(size).withPage(page));
    }

    @Override
    @Transactional
    public void update(UUID id, User user, LocalDateTime version) {
        Optional<User> optionalPerson = userRepository.findById(id);

        User existingUser = optionalPerson.orElseThrow(() -> new NotFoundException("User with id " + id + " not found"));

        if (!existingUser.getVersion().equals(version)) {
            throw new WrongVersionException("User with id " + id + " has been updated");
        }

        if (!existingUser.getEmail().equals(user.getEmail()) && userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new NotUniqueException("User with email " + user.getEmail() + " already exists");
        }

        existingUser.setUsername(user.getUsername());
        existingUser.setEmail(user.getEmail());
        existingUser.setPassword(passwordEncoder.encode(user.getPassword()));

        userRepository.save(existingUser);
    }

    @Override
    @Transactional
    public void delete(UUID id, LocalDateTime version) {
        Optional<User> optionalPerson = userRepository.findById(id);

        User existingUser = optionalPerson.orElseThrow(() -> new NotFoundException("User with id " + id + " not found"));

        if (!existingUser.getVersion().equals(version)) {
            throw new WrongVersionException("User with id " + id + " has been updated");
        }

        userRepository.delete(existingUser);
    }

    @Override
    @Transactional
    public User updateRole(UUID id, String role, LocalDateTime version) {
        if (!roles.contains(role)) {
            throw new IllegalArgumentException("Role " + role + " is not supported");
        }

        Optional<User> optionalPerson = userRepository.findById(id);

        User existingUser = optionalPerson.orElseThrow(() -> new NotFoundException("User with id " + id + " not found"));

        if (!existingUser.getVersion().equals(version)) {
            throw new WrongVersionException("User with id " + id + " has been updated");
        }

        existingUser.setRole(role);

        userRepository.save(existingUser);

        return existingUser;
    }
}