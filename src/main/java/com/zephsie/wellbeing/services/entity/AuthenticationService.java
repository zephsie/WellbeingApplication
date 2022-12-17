package com.zephsie.wellbeing.services.entity;

import com.zephsie.wellbeing.models.entity.User;
import com.zephsie.wellbeing.models.entity.VerificationToken;
import com.zephsie.wellbeing.repositories.UserRepository;
import com.zephsie.wellbeing.repositories.VerificationTokenRepository;
import com.zephsie.wellbeing.services.api.IAuthenticationService;
import com.zephsie.wellbeing.utils.exceptions.InvalidCredentialException;
import com.zephsie.wellbeing.utils.exceptions.NotUniqueException;
import com.zephsie.wellbeing.utils.exceptions.ValidationException;
import com.zephsie.wellbeing.utils.random.api.IRandomTokenProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class AuthenticationService implements IAuthenticationService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final VerificationTokenRepository verificationTokenRepository;

    private final IRandomTokenProvider randomTokenProvider;

    @Value("${role.default}")
    private String ROLE_DEFAULT;

    @Value("${active.default}")
    private boolean ACTIVE_DEFAULT;

    public AuthenticationService(UserRepository userRepository, PasswordEncoder passwordEncoder, VerificationTokenRepository verificationTokenRepository, IRandomTokenProvider randomTokenProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.verificationTokenRepository = verificationTokenRepository;
        this.randomTokenProvider = randomTokenProvider;
    }

    @Override
    @Transactional
    public VerificationToken register(User user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new NotUniqueException("User with email " + user.getEmail() + " already exists");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(ROLE_DEFAULT);
        user.setIsActive(ACTIVE_DEFAULT);

        userRepository.save(user);

        VerificationToken verificationToken = new VerificationToken(randomTokenProvider.generate(), user);

        verificationTokenRepository.save(verificationToken);

        return verificationToken;
    }

    @Override
    @Transactional
    public void verifyUser(VerificationToken verificationToken) {
        Optional<User> optionalUser = userRepository.findByEmail(verificationToken.getUser().getEmail());

        User user = optionalUser.orElseThrow(() -> new InvalidCredentialException("User with email " + verificationToken.getUser().getEmail() + " not found"));

        if (user.getIsActive()) {
            throw new ValidationException("User with email " + verificationToken.getUser().getEmail() + " already verified");
        }

        VerificationToken verificationTokenFromDb = verificationTokenRepository.findByUser(user).orElseThrow(() -> new ValidationException("Verification token not found"));

        if (!verificationToken.getToken().equals(verificationTokenFromDb.getToken())) {
            throw new InvalidCredentialException("Invalid verification token");
        }

        user.setIsActive(true);

        userRepository.save(user);
    }

    @Override
    @Transactional
    public VerificationToken refreshVerificationToken(User user) {
        Optional<User> userOptional = userRepository.findByEmail(user.getEmail());

        User userFromDB = userOptional.orElseThrow(() -> new InvalidCredentialException("User with email " + user.getEmail() + " not found"));

        if (userFromDB.getIsActive()) {
            throw new ValidationException("User already verified");
        }

        if (!passwordEncoder.matches(user.getPassword(), userFromDB.getPassword())) {
            throw new InvalidCredentialException("Invalid credentials");
        }

        Optional<VerificationToken> verificationTokenOptional = verificationTokenRepository.findByUser(userFromDB);

        verificationTokenOptional.ifPresent(verificationTokenRepository::delete);

        VerificationToken verificationToken = new VerificationToken(randomTokenProvider.generate(), userFromDB);

        return verificationTokenRepository.save(verificationToken);
    }
}