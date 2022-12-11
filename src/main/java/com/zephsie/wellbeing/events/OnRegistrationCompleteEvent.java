package com.zephsie.wellbeing.events;

import com.zephsie.wellbeing.models.entity.VerificationToken;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class OnRegistrationCompleteEvent extends ApplicationEvent {
    private final VerificationToken verificationToken;

    public OnRegistrationCompleteEvent(VerificationToken verificationToken) {
        super(verificationToken);
        this.verificationToken = verificationToken;
    }
}