package com.zephsie.wellbeing.utils.json.converters;

import com.zephsie.wellbeing.utils.json.custom.CustomPage;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class PageJSONConverter {
    public CustomPage convertPage(Page<?> page) {
        return new CustomPage(Map.of(
                "number", page.getNumber(),
                "size", page.getSize(),
                "total_pages", page.getTotalPages(),
                "total_elements", page.getTotalElements(),
                "first", page.isFirst(),
                "number_of_elements", page.getNumberOfElements(),
                "last", page.isLast(),
                "content", page.getContent()
        ));
    }
}