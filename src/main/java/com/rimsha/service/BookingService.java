package com.rimsha.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rimsha.exceptions.RoomsNotFoundException;
import com.rimsha.exceptions.ValidationException;
import com.rimsha.model.db.entity.Booking;
import com.rimsha.model.db.entity.Room;
import com.rimsha.model.db.repository.BookingRepository;
import com.rimsha.model.db.repository.RoomRepository;
import com.rimsha.model.db.repository.UserRepository;
import com.rimsha.model.dto.request.BookingRequest;
import com.rimsha.model.dto.response.BookingResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;
    private final ObjectMapper mapper;
    private final UserRepository userRepository;


    @Transactional
    public BookingResponse createBooking(BookingRequest request) {
        UserDetails principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        com.rimsha.model.db.entity.User user = userRepository.findByEmailIgnoreCase(principal.getUsername())
                .orElseThrow(() -> new ValidationException(String.format("User with email: %s not found",
                        principal.getUsername()), HttpStatus.NOT_FOUND));

        Room room = roomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new RoomsNotFoundException(String.format("Room with %d not found", request.getRoomId()), HttpStatus.NOT_FOUND));
        // Проверка доступности комнаты на указанные даты
        if (isRoomAvailable(request.getRoomId(), request.getCheckInDate(), request.getCheckOutDate())) {
            Booking booking = new Booking();
            booking.setRoom(room);
            booking.setCheckInDate(request.getCheckInDate());
            booking.setCheckOutDate(request.getCheckOutDate());
            booking.setUser(user);

            // Сохраняем бронирование
            Booking savedBooking = bookingRepository.save(booking);
            return mapper.convertValue(savedBooking, BookingResponse.class);

        } else {
            throw new ValidationException("Room is not available for the selected dates", HttpStatus.BAD_REQUEST);
        }

    }

    // Метод для проверки, доступна ли комната на указанные даты
    public boolean isRoomAvailable(Long roomId, LocalDate checkInDate, LocalDate checkOutDate) {
        List<Booking> existingBookings = bookingRepository.findBookingsForRoomAndDates(roomId, checkInDate, checkOutDate);
        return existingBookings.isEmpty();
    }
}
