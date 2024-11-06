package com.rimsha.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rimsha.exceptions.EntityNotFoundException;
import com.rimsha.exceptions.ValidationException;
import com.rimsha.model.db.entity.Booking;
import com.rimsha.model.db.entity.Payment;
import com.rimsha.model.db.entity.Room;
import com.rimsha.model.db.entity.User;
import com.rimsha.model.db.repository.BookingRepository;
import com.rimsha.model.db.repository.RoomRepository;
import com.rimsha.model.dto.request.BookingRequest;
import com.rimsha.model.dto.response.BookingResponse;
import com.rimsha.model.enums.BookingStatus;
import com.rimsha.model.enums.PaymentStatus;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;
    private final ObjectMapper mapper;
    private final UserService userService;



    @Transactional
    public BookingResponse createBooking(BookingRequest request) {
        UserDetails principal = userService.getPrincipal();
        User user = userService.getUserByEmail(principal.getUsername());

        Room room = roomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new EntityNotFoundException(String.format("Room with %d not found", request.getRoomId()), HttpStatus.NOT_FOUND));
        // Проверка доступности комнаты на указанные даты
        if (isRoomAvailable(request.getRoomId(), request.getCheckInDate(), request.getCheckOutDate())) {
            Booking booking = new Booking();
            booking.setRoom(room);
            booking.setCheckInDate(request.getCheckInDate());
            booking.setCheckOutDate(request.getCheckOutDate());

            booking.setUser(user);

            Payment payment = new Payment();
            payment.setStatus(PaymentStatus.PENDING);

            // Сохраняем бронирование
            Booking savedBooking = bookingRepository.save(booking);
            BookingResponse bookingResponse = mapper.convertValue(savedBooking, BookingResponse.class);
            bookingResponse.setMessage("Successfully created booking");
            bookingResponse.setRoomType(room.getRoomType());
            bookingResponse.setBookingId(savedBooking.getId());


            return bookingResponse;

        } else {
            throw new ValidationException("Room is not available for the selected dates", HttpStatus.BAD_REQUEST);
        }
    }

    // Метод для проверки, доступна ли комната на указанные даты
    public boolean isRoomAvailable(Long roomId, LocalDate checkInDate, LocalDate checkOutDate) {
        List<Booking> existingBookings = bookingRepository.findBookingsForRoomAndDates(roomId, checkInDate, checkOutDate);
        return existingBookings.isEmpty();
    }

    private Booking getBookingFromDB(Long id) {
        return bookingRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.format("Booking with id: %s not found", id), HttpStatus.NOT_FOUND)
        );
    }

    public BookingResponse getBooking(Long id) {
        Booking booking = getBookingFromDB(id);
        BookingResponse bookingResponse = new BookingResponse();
        bookingResponse.setBookingId(booking.getId());
        bookingResponse.setCheckOutDate(booking.getCheckOutDate());
        bookingResponse.setCheckInDate(booking.getCheckInDate());
        bookingResponse.setRoomType(booking.getRoom().getRoomType());
        bookingResponse.setMessage("Successfully get booking");

        return bookingResponse;
    }

    public BookingResponse updateBooking(Long id, BookingRequest request) {
        Booking booking = getBookingFromDB(id);
        UserDetails principal = userService.getPrincipal();
        User user = userService.getUserByEmail(principal.getUsername());
        if (!booking.getUser().getId().equals(user.getId())) {
            throw new ValidationException("No permission for user: " + user.getEmail(), HttpStatus.UNAUTHORIZED);
        }
        if (!isRoomAvailable(request.getRoomId(), request.getCheckInDate(), request.getCheckOutDate())) {
            throw new ValidationException("Room is not available for the selected dates", HttpStatus.BAD_REQUEST);
        }
        booking.setCheckInDate(request.getCheckInDate());
        booking.setCheckOutDate(request.getCheckOutDate());

        booking.setUpdatedAt(LocalDateTime.now());
        booking.setStatus(BookingStatus.UPDATED);
        bookingRepository.save(booking);
        return mapper.convertValue(booking, BookingResponse.class);
    }

    public void deleteBooking(Long id) {
        Booking booking = getBookingFromDB(id);
        UserDetails principal = userService.getPrincipal();
        User user = userService.getUserByEmail(principal.getUsername());
        if (!booking.getUser().getId().equals(user.getId())) {
            throw new ValidationException("No permission for user: " + user.getEmail(), HttpStatus.UNAUTHORIZED);
        }
        booking.setUpdatedAt(LocalDateTime.now());
        booking.setStatus(BookingStatus.DELETED);
        bookingRepository.save(booking);
    }

    public List<BookingResponse> getAllBookings() {
        return bookingRepository.findAll().stream()
                .map(booking -> mapper.convertValue(booking, BookingResponse.class))
                .collect(Collectors.toList());
    }
}
