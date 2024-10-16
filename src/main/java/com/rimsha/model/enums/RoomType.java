package com.rimsha.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RoomType {

    SINGLE_STANDARD("Одноместный стандартный"),
    SINGLE_COMFORT("Одноместный комфортный"),
    DOUBLE_STANDARD("Двухместный стандартный"),
    DOUBLE_COMFORT("Двухместный комфортный");

    private final String description;
}
