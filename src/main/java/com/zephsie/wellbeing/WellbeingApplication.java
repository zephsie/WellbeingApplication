package com.zephsie.wellbeing;

import com.zephsie.wellbeing.models.entity.User;
import com.zephsie.wellbeing.services.api.IAuthenticationService;
import com.zephsie.wellbeing.services.api.IUserService;
import com.zephsie.wellbeing.utils.exceptions.NotUniqueException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories
@Slf4j
public class WellbeingApplication {
    private final IUserService userService;

    private final IAuthenticationService authenticationService;

    @Autowired
    public WellbeingApplication(IUserService userService, IAuthenticationService authenticationService) {
        this.userService = userService;
        this.authenticationService = authenticationService;
    }

    public static void main(String[] args) {
        SpringApplication.run(WellbeingApplication.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
        return args -> {
            User user = new User();
            user.setEmail("admin@admin.admin");
            user.setPassword("admin");
            user.setUsername("admin");

            try {
                authenticationService.register(user);

                userService.updateRole(user.getId(), "ROLE_ADMIN", user.getVersion());

                log.info("Admin user created");
            } catch (NotUniqueException e) {
                log.info("Admin user already exists");
            }

            log.info("Email: " + user.getEmail() + " Password: " + user.getPassword());
        };
    }
}