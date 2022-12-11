package com.zephsie.wellbeing.services.entity;

import com.zephsie.wellbeing.configs.security.UserDetailsImp;
import com.zephsie.wellbeing.models.entity.User;
import com.zephsie.wellbeing.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;

    @Autowired
    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByEmail(s);

        if (user.isEmpty()) {
            throw new UsernameNotFoundException("User with email " + s + " not found");
        }

        return new UserDetailsImp(user.get());
    }
}
