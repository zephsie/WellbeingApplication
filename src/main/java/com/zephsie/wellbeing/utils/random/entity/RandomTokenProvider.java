package com.zephsie.wellbeing.utils.random.entity;

import com.zephsie.wellbeing.utils.random.api.IRandomTokenProvider;
import org.springframework.stereotype.Component;

@Component
public class RandomTokenProvider implements IRandomTokenProvider {
    @Override
    public String generate() {
        return String.format("%04d", (int) (Math.random() * 10000));
    }
}