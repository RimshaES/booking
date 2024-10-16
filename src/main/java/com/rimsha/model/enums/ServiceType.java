package com.rimsha.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ServiceType {

    BREAKFAST("Завтрак"),
    PARKING("Паркинг"),
    CLEANING("Уборка");

    private final String description;
}
