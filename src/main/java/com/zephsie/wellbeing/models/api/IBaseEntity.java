package com.zephsie.wellbeing.models.api;

import java.time.LocalDateTime;

public interface IBaseEntity<ID> {
    ID getId();

    void setId(ID id);

    LocalDateTime getDtUpdate();

    LocalDateTime getDtCreate();
}
