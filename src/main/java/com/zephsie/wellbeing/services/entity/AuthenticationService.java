package com.zephsie.wellbeing.services.entity;

import com.zephsie.wellbeing.dtos.VerificationDTO;
import com.zephsie.wellbeing.models.entity.User;
import com.zephsie.wellbeing.models.entity.VerificationToken;
import com.zephsie.wellbeing.repositories.UserRepository;
import com.zephsie.wellbeing.repositories.VerificationTokenRepository;
import com.zephsie.wellbeing.utils.exceptions.NotUniqueException;
import com.zephsie.wellbeing.utils.exceptions.ValidationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
public class AuthenticationService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final VerificationTokenRepository verificationTokenRepository;

    @Value("${role.default}")
    private String ROLE_DEFAULT;

    @Value("${active.default}")
    private boolean ACTIVE_DEFAULT;

    public AuthenticationService(UserRepository userRepository, PasswordEncoder passwordEncoder, VerificationTokenRepository verificationTokenRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.verificationTokenRepository = verificationTokenRepository;
    }

    @Transactional
    public VerificationToken register(User user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new NotUniqueException("User with email " + user.getEmail() + " already exists");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(ROLE_DEFAULT);
        user.setIsActive(ACTIVE_DEFAULT);

        userRepository.save(user);

        VerificationToken verificationToken = new VerificationToken(UUID.randomUUID().toString(), user);

        verificationTokenRepository.save(verificationToken);

        return verificationToken;
    }

    @Transactional
    public void verifyUser(VerificationDTO verificationDTO) {
        Optional<VerificationToken> verificationTokenOptional = verificationTokenRepository.findByToken(verificationDTO.getToken());

        if (verificationTokenOptional.isPresent() && verificationTokenOptional.get().getUser().getIsActive()) {
            throw new ValidationException("User already verified");
        }

        if (verificationTokenOptional.isEmpty()) {
            throw new ValidationException("Invalid token");
        }

        VerificationToken verificationToken = verificationTokenOptional.get();

        User user = verificationToken.getUser();

        if (!verificationDTO.getEmail().equals(user.getEmail()) || !passwordEncoder.matches(verificationDTO.getPassword(), user.getPassword())) {
            throw new ValidationException("Invalid credentials");
        }

        user.setIsActive(true);

        userRepository.save(user);

        verificationTokenRepository.delete(verificationToken);
    }

    @Transactional
    public VerificationToken refreshVerificationToken(User user) {
        Optional<User> userOptional = userRepository.findByEmail(user.getEmail());

        if (userOptional.isEmpty()) {
            throw new ValidationException("User not found");
        }

        User userFromDB = userOptional.get();

        if (userFromDB.getIsActive()) {
            throw new ValidationException("User already verified");
        }

        if (!passwordEncoder.matches(user.getPassword(), userFromDB.getPassword())) {
            throw new ValidationException("Invalid credentials");
        }

        Optional<VerificationToken> verificationTokenOptional = verificationTokenRepository.findByUser(userFromDB);

        verificationTokenOptional.ifPresent(verificationTokenRepository::delete);

        VerificationToken verificationToken = new VerificationToken(UUID.randomUUID().toString(), userFromDB);

        return verificationTokenRepository.save(verificationToken);
    }
}