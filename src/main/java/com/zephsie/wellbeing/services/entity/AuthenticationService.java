package com.zephsie.wellbeing.services.entity;

import com.zephsie.wellbeing.dtos.LoginDTO;
import com.zephsie.wellbeing.dtos.UserDTO;
import com.zephsie.wellbeing.dtos.VerificationDTO;
import com.zephsie.wellbeing.models.entity.Role;
import com.zephsie.wellbeing.models.entity.User;
import com.zephsie.wellbeing.models.entity.VerificationToken;
import com.zephsie.wellbeing.repositories.UserRepository;
import com.zephsie.wellbeing.repositories.VerificationTokenRepository;
import com.zephsie.wellbeing.services.api.IAuthenticationService;
import com.zephsie.wellbeing.utils.exceptions.InvalidCredentialException;
import com.zephsie.wellbeing.utils.exceptions.NotUniqueException;
import com.zephsie.wellbeing.utils.exceptions.ValidationException;
import com.zephsie.wellbeing.utils.random.api.IRandomTokenProvider;
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

    public AuthenticationService(UserRepository userRepository,
                                 PasswordEncoder passwordEncoder,
                                 VerificationTokenRepository verificationTokenRepository,
                                 IRandomTokenProvider randomTokenProvider) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.verificationTokenRepository = verificationTokenRepository;
        this.randomTokenProvider = randomTokenProvider;
    }

    @Override
    @Transactional
    public VerificationToken register(UserDTO userDTO) {
        if (userRepository.existsByEmail(userDTO.getEmail())) {
            throw new NotUniqueException("User with email " + userDTO.getEmail() + " already exists");
        }

        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setRole(Role.ROLE_USER);
        user.setIsActive(false);

        VerificationToken verificationToken = new VerificationToken(randomTokenProvider.generate(), user);
        verificationTokenRepository.save(verificationToken);
        return verificationToken;
    }

    @Override
    @Transactional
    public User verifyUser(VerificationDTO verificationDTO) {
        Optional<User> optionalUser = userRepository.findByEmail(verificationDTO.getEmail());

        User user = optionalUser.orElseThrow(() -> new InvalidCredentialException("User with email " + verificationDTO.getEmail() + " not found"));

        if (!passwordEncoder.matches(verificationDTO.getPassword(), user.getPassword())) {
            throw new InvalidCredentialException("Invalid password");
        }

        if (user.getIsActive()) {
            throw new ValidationException("User with email " + verificationDTO.getEmail() + " already verified");
        }

        VerificationToken verificationTokenFromDb = verificationTokenRepository.findByUser(user)
                .orElseThrow(() -> new ValidationException("Verification token not found"));

        if (!verificationDTO.getToken().equals(verificationTokenFromDb.getToken())) {
            throw new InvalidCredentialException("Invalid verification token");
        }

        user.setIsActive(true);

        verificationTokenRepository.delete(verificationTokenFromDb);

        return userRepository.save(user);
    }

    @Override
    @Transactional
    public VerificationToken refreshVerificationToken(LoginDTO loginDTO) {
        Optional<User> userOptional = userRepository.findByEmail(loginDTO.getEmail());

        User userFromDB = userOptional.orElseThrow(() -> new InvalidCredentialException("User with email " + loginDTO.getEmail() + " not found"));

        if (!passwordEncoder.matches(loginDTO.getPassword(), userFromDB.getPassword())) {
            throw new InvalidCredentialException("Invalid password");
        }

        if (userFromDB.getIsActive()) {
            throw new ValidationException("User already verified");
        }

        verificationTokenRepository.deleteByUser(userFromDB);

        return verificationTokenRepository.save(new VerificationToken(randomTokenProvider.generate(), userFromDB));
    }
}