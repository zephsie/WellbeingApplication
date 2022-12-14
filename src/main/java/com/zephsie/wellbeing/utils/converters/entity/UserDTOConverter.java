package com.zephsie.wellbeing.utils.converters.entity;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zephsie.wellbeing.dtos.UserDTO;
import com.zephsie.wellbeing.models.entity.User;
import com.zephsie.wellbeing.utils.converters.api.IEntityDTOConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserDTOConverter implements IEntityDTOConverter<User, UserDTO> {

    private final ObjectMapper objectMapper;

    @Autowired
    public UserDTOConverter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public User convertToEntity(UserDTO UserDTO) {
        return objectMapper.convertValue(UserDTO, User.class);
    }
}