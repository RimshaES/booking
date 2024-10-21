package com.rimsha.exceptions;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public class EntityNotFoundException extends RuntimeException {

    private final String message;
    private final HttpStatus status;
}