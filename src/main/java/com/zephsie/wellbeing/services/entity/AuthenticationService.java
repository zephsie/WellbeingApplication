package com.zephsie.wellbeing.services.entity;

import com.zephsie.wellbeing.models.entity.User;
import com.zephsie.wellbeing.models.entity.VerificationToken;
import com.zephsie.wellbeing.repositories.UserRepository;
import com.zephsie.wellbeing.repositories.VerificationTokenRepository;
import com.zephsie.wellbeing.services.api.IAuthenticationService;
import com.zephsie.wellbeing.utils.converters.UnixTimeToLocalDateTimeConverter;
import com.zephsie.wellbeing.utils.exceptions.InvalidCredentialException;
import com.zephsie.wellbeing.utils.exceptions.NotUniqueException;
import com.zephsie.wellbeing.utils.exceptions.ValidationException;
import com.zephsie.wellbeing.utils.random.api.IRandomTokenProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
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

    @Value("${token.expiration}")
    private long TOKEN_EXPIRATION;

    private final Clock clock;

    private final UnixTimeToLocalDateTimeConverter unixTimeToLocalDateTimeConverter;

    public AuthenticationService(UserRepository userRepository, PasswordEncoder passwordEncoder, VerificationTokenRepository verificationTokenRepository, IRandomTokenProvider randomTokenProvider, Clock clock, UnixTimeToLocalDateTimeConverter unixTimeToLocalDateTimeConverter) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.verificationTokenRepository = verificationTokenRepository;
        this.randomTokenProvider = randomTokenProvider;
        this.clock = clock;
        this.unixTimeToLocalDateTimeConverter = unixTimeToLocalDateTimeConverter;
    }

    @Override
    @Transactional
    public VerificationToken register(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new NotUniqueException("User with email " + user.getEmail() + " already exists");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(ROLE_DEFAULT);
        user.setIsActive(ACTIVE_DEFAULT);

        VerificationToken verificationToken = new VerificationToken(randomTokenProvider.generate(), user);
        verificationTokenRepository.save(verificationToken);
        return verificationToken;
    }

    @Override
    @Transactional
    public void verifyUser(VerificationToken verificationToken) {
        Optional<User> optionalUser = userRepository.findByEmail(verificationToken.getUser().getEmail());

        User user = optionalUser.orElseThrow(() -> new InvalidCredentialException("User with email " + verificationToken.getUser().getEmail() + " not found"));

        if (!passwordEncoder.matches(verificationToken.getUser().getPassword(), user.getPassword())) {
            throw new InvalidCredentialException("Invalid password");
        }

        if (user.getIsActive()) {
            throw new ValidationException("User with email " + verificationToken.getUser().getEmail() + " already verified");
        }

        VerificationToken verificationTokenFromDb = verificationTokenRepository.findByUser(user).orElseThrow(() -> new ValidationException("Verification token not found"));

        if (verificationTokenFromDb.getVersion().isBefore(unixTimeToLocalDateTimeConverter.convert(clock.millis() - TOKEN_EXPIRATION))) {
            throw new InvalidCredentialException("Verification token expired. Request new one");
        }

        if (!verificationToken.getToken().equals(verificationTokenFromDb.getToken())) {
            throw new InvalidCredentialException("Invalid verification token");
        }

        user.setIsActive(true);

        verificationTokenRepository.delete(verificationTokenFromDb);

        userRepository.save(user);
    }

    @Override
    @Transactional
    public VerificationToken refreshVerificationToken(User user) {
        Optional<User> userOptional = userRepository.findByEmail(user.getEmail());

        User userFromDB = userOptional.orElseThrow(() -> new InvalidCredentialException("User with email " + user.getEmail() + " not found"));

        if (!passwordEncoder.matches(user.getPassword(), userFromDB.getPassword())) {
            throw new InvalidCredentialException("Invalid password");
        }

        if (userFromDB.getIsActive()) {
            throw new ValidationException("User already verified");
        }

        Optional<VerificationToken> verificationTokenOptional = verificationTokenRepository.findByUser(userFromDB);

        VerificationToken verificationToken = verificationTokenOptional.map(v -> {
            v.setToken(randomTokenProvider.generate());
            return v;
        }).orElseGet(() -> new VerificationToken(randomTokenProvider.generate(), userFromDB));

        verificationTokenRepository.save(verificationToken);
        return verificationToken;
    }
}