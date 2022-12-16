package com.zephsie.wellbeing.utils.random.entity;

import com.zephsie.wellbeing.utils.random.api.IRandomTokenProvider;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class RandomTokenProvider implements IRandomTokenProvider {
    @Override
    public String generate() {
        return UUID.randomUUID().toString();
    }
}