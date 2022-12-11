package com.zephsie.wellbeing.services.entity;

import com.zephsie.wellbeing.events.OnRegistrationCompleteEvent;
import com.zephsie.wellbeing.models.entity.User;
import com.zephsie.wellbeing.models.entity.VerificationToken;
import com.zephsie.wellbeing.utils.exceptions.EmailException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class EmailVerificationService {

    private final ApplicationEventPublisher eventPublisher;

    @Autowired
    public EmailVerificationService(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    public void sendVerificationEmail(VerificationToken verificationToken) {
        eventPublisher.publishEvent(new OnRegistrationCompleteEvent(verificationToken));
    }
}