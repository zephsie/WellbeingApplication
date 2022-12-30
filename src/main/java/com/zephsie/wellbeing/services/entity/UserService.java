package com.zephsie.wellbeing.services.entity;

import com.zephsie.wellbeing.dtos.NewUserDTO;
import com.zephsie.wellbeing.dtos.UserDTO;
import com.zephsie.wellbeing.models.entity.Role;
import com.zephsie.wellbeing.models.entity.Status;
import com.zephsie.wellbeing.models.entity.User;
import com.zephsie.wellbeing.repositories.UserRepository;
import com.zephsie.wellbeing.services.api.IUserService;
import com.zephsie.wellbeing.utils.converters.api.IEntityDTOConverter;
import com.zephsie.wellbeing.utils.exceptions.NotFoundException;
import com.zephsie.wellbeing.utils.exceptions.NotUniqueException;
import com.zephsie.wellbeing.utils.exceptions.ValidationException;
import com.zephsie.wellbeing.utils.exceptions.WrongVersionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService implements IUserService {
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final IEntityDTOConverter<User, UserDTO> userDTOConverter;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, IEntityDTOConverter<User, UserDTO> userDTOConverter) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userDTOConverter = userDTOConverter;
    }

    @Override
    @Transactional
    public User create(UserDTO userDTO) {
        if (userRepository.existsByEmail(userDTO.getEmail())) {
            throw new NotUniqueException("User with email " + userDTO.getEmail() + " already exists");
        }

        User user = userDTOConverter.convertToEntity(userDTO);

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        return userRepository.save(user);
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
    public User update(UUID id, NewUserDTO newUserDTO, LocalDateTime version) {
        Optional<User> optionalPerson = userRepository.findById(id);

        User existingUser = optionalPerson.orElseThrow(() -> new NotFoundException("User with id " + id + " not found"));

        if (!existingUser.getDtUpdate().equals(version)) {
            throw new WrongVersionException("User with id " + id + " has been updated");
        }

        if (!existingUser.getEmail().equals(newUserDTO.getEmail()) && userRepository.findByEmail(newUserDTO.getEmail()).isPresent()) {
            throw new NotUniqueException("User with email " + newUserDTO.getEmail() + " already exists");
        }

        existingUser.setUsername(newUserDTO.getUsername());
        existingUser.setEmail(newUserDTO.getEmail());
        existingUser.setPassword(passwordEncoder.encode(newUserDTO.getPassword()));

        return userRepository.save(existingUser);
    }

    @Override
    @Transactional
    public User updateRole(UUID id, Role role, LocalDateTime version) {
        Optional<User> optionalPerson = userRepository.findById(id);

        User existingUser = optionalPerson.orElseThrow(() -> new NotFoundException("User with id " + id + " not found"));

        if (!existingUser.getDtUpdate().equals(version)) {
            throw new WrongVersionException("User with id " + id + " has been updated");
        }

        existingUser.setRole(role);

        userRepository.save(existingUser);

        return existingUser;
    }

    @Override
    @Transactional
    public User updateStatus(UUID id, Status status, LocalDateTime version) {
        Optional<User> optionalPerson = userRepository.findById(id);

        User existingUser = optionalPerson.orElseThrow(() -> new NotFoundException("User with id " + id + " not found"));

        if (!existingUser.getDtUpdate().equals(version)) {
            throw new WrongVersionException("User with id " + id + " has been updated");
        }

        existingUser.setStatus(status);

        userRepository.save(existingUser);

        return existingUser;
    }

    @Override
    @Transactional
    public void delete(UUID id, LocalDateTime version) {
        Optional<User> optionalPerson = userRepository.findById(id);

        User existingUser = optionalPerson.orElseThrow(() -> new NotFoundException("User with id " + id + " not found"));

        if (!existingUser.getDtUpdate().equals(version)) {
            throw new WrongVersionException("User with id " + id + " has been updated");
        }

        if (existingUser.getRole().equals(Role.ROLE_ADMIN) && userRepository.countByRole(Role.ROLE_ADMIN) == 1) {
            throw new ValidationException("User with id " + id + " is the only admin");
        }

        userRepository.delete(existingUser);
    }
}