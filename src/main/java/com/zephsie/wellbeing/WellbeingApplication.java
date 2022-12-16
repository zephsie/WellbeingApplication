package com.zephsie.wellbeing;

import com.zephsie.wellbeing.dtos.VerificationDTO;
import com.zephsie.wellbeing.models.entity.VerificationToken;
import com.zephsie.wellbeing.utils.converters.api.IEntityDTOConverter;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import java.time.Clock;

@SpringBootApplication
@Slf4j
public class WellbeingApplication {
    public static void main(String[] args) {
        SpringApplication.run(WellbeingApplication.class, args);
    }

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    @Bean
    public Clock clock() {
        return Clock.systemDefaultZone();
    }

    private final IEntityDTOConverter<VerificationToken, VerificationDTO> verificationTokenDTOConverter;

    @Autowired
    public WellbeingApplication(IEntityDTOConverter<VerificationToken, VerificationDTO> verificationTokenDTOConverter) {
        this.verificationTokenDTOConverter = verificationTokenDTOConverter;
    }

    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
        return args -> {
            VerificationDTO verificationDTO = new VerificationDTO("token", "email", "username");
            VerificationToken verificationToken = verificationTokenDTOConverter.convertToEntity(verificationDTO);
            log.info("VerificationToken: {}", verificationToken.getToken() + " " + verificationToken.getUser().getEmail() + " " + verificationToken.getUser().getUsername());
        };
    }
}