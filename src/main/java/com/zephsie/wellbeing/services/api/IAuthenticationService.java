package com.zephsie.wellbeing.services.api;

import com.zephsie.wellbeing.models.entity.User;
import com.zephsie.wellbeing.models.entity.VerificationToken;

public interface IAuthenticationService {
    VerificationToken register(User user);

    void verifyUser(VerificationToken verificationToken);

    VerificationToken refreshVerificationToken(User user);
}