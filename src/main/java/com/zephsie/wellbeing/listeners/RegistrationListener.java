package com.zephsie.wellbeing.listeners;

import com.zephsie.wellbeing.events.OnRegistrationCompleteEvent;
import com.zephsie.wellbeing.models.entity.VerificationToken;
import com.zephsie.wellbeing.services.entity.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.lang.NonNull;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class RegistrationListener implements ApplicationListener<OnRegistrationCompleteEvent> {

    private final JavaMailSender mailSender;

    @Autowired
    public RegistrationListener(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void onApplicationEvent(@NonNull OnRegistrationCompleteEvent event) {
        this.confirmRegistration(event);
    }

    private void confirmRegistration(OnRegistrationCompleteEvent event) {
        VerificationToken verificationToken = event.getVerificationToken();

        String recipientAddress = verificationToken.getUser().getEmail();
        String subject = "Registration Confirmation";
        String message = "Your verification code is " + verificationToken.getToken();

        SimpleMailMessage email = new SimpleMailMessage();

        email.setTo(recipientAddress);
        email.setSubject(subject);
        email.setText(message);

        mailSender.send(email);
    }
}