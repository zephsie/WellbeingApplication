package com.zephsie.wellbeing.utils.converters.entity;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zephsie.wellbeing.dtos.LoginDTO;
import com.zephsie.wellbeing.models.entity.User;
import com.zephsie.wellbeing.utils.converters.api.IEntityDTOConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserLoginDTOConverter implements IEntityDTOConverter<User, LoginDTO> {

    private final ObjectMapper objectMapper;

    @Autowired
    public UserLoginDTOConverter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }


    @Override
    public User convertToEntity(LoginDTO dto) {
        return objectMapper.convertValue(dto, User.class);
    }
}
