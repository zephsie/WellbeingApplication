package com.zephsie.wellbeing.utils.converters.entity;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zephsie.wellbeing.dtos.NewUserDTO;
import com.zephsie.wellbeing.models.entity.User;
import com.zephsie.wellbeing.utils.converters.api.IEntityDTOConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class NewUserDTOConverter implements IEntityDTOConverter<User, NewUserDTO> {

    private final ObjectMapper objectMapper;

    @Autowired
    public NewUserDTOConverter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public User convertToEntity(NewUserDTO newUserDTO) {
        return objectMapper.convertValue(newUserDTO, User.class);
    }
}