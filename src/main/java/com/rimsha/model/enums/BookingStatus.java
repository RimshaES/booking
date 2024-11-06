package com.rimsha.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BookingStatus {
    CREATED("Создан"),
    UPDATED("Обновлен"),
    DELETED("Удален");

    private final String description;
}