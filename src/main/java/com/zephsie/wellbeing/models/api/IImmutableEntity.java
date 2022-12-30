package com.zephsie.wellbeing.models.api;

import java.time.LocalDateTime;

public interface IImmutableEntity<ID> {
    ID getId();

    void setId(ID id);

    LocalDateTime getDtCreate();
}
