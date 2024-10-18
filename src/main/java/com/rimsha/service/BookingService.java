package com.rimsha.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rimsha.model.db.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final ObjectMapper mapper;
}
