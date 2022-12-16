package com.zephsie.wellbeing.utils.converters.entity;

import com.zephsie.wellbeing.dtos.VerificationDTO;
import com.zephsie.wellbeing.models.entity.User;
import com.zephsie.wellbeing.models.entity.VerificationToken;
import com.zephsie.wellbeing.utils.converters.api.IEntityDTOConverter;
import org.springframework.stereotype.Component;

@Component
public class VerificationTokenDTOConverter implements IEntityDTOConverter<VerificationToken, VerificationDTO> {

    @Override
    public VerificationToken convertToEntity(VerificationDTO dto) {
        User user = new User();
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());

        return new VerificationToken(dto.getToken(), user);
    }

    @Override
    public VerificationDTO convertToDTO(VerificationToken entity) {
        return new VerificationDTO(entity.getToken(), entity.getUser().getEmail(), entity.getUser().getPassword());
    }
}