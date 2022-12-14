package com.zephsie.wellbeing.services.api;

import com.zephsie.wellbeing.dtos.LoginDTO;
import com.zephsie.wellbeing.dtos.NewUserDTO;
import com.zephsie.wellbeing.dtos.VerificationDTO;
import com.zephsie.wellbeing.models.entity.User;
import com.zephsie.wellbeing.models.entity.VerificationToken;

public interface IAuthenticationService {
    VerificationToken register(NewUserDTO newUserDTO);

    User verifyUser(VerificationDTO verificationDTO);

    VerificationToken refreshVerificationToken(LoginDTO loginDTO);
}