package com.rimsha.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rimsha.model.db.entity.Room;
import com.rimsha.model.db.repository.BookingRepository;
import com.rimsha.model.db.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;
    private final ObjectMapper mapper;



}
