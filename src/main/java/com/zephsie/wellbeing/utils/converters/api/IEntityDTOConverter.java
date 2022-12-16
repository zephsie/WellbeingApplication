package com.zephsie.wellbeing.utils.converters.api;

public interface IEntityDTOConverter<E, D> {
    E convertToEntity(D dto);

    D convertToDTO(E entity);
}
