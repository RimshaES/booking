package com.rimsha.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.rimsha.AbstractTest;
import com.rimsha.exceptions.EntityNotFoundException;
import com.rimsha.exceptions.ValidationException;
import com.rimsha.model.db.entity.Booking;
import com.rimsha.model.db.entity.Room;
import com.rimsha.model.db.entity.User;
import com.rimsha.model.db.repository.BookingRepository;
import com.rimsha.model.db.repository.RoomRepository;
import com.rimsha.model.db.repository.UserRepository;
import com.rimsha.model.dto.request.BookingRequest;
import com.rimsha.model.dto.response.BookingResponse;
import com.rimsha.model.enums.BookingStatus;
import com.rimsha.model.enums.RoomType;
import com.rimsha.model.enums.UserStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalDate;
import java.util.List;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
public class BookingServiceTest extends AbstractTest {

    BookingService bookingService;

    @Autowired
    UserService userService;

    @Autowired
    BookingRepository bookingRepository;

    @Autowired
    RoomRepository roomRepository;

    @Autowired
    UserRepository userRepository;


    @BeforeEach
    void setUp() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        bookingService = new BookingService(bookingRepository, roomRepository, mapper, userService);
    }

    @AfterEach
    void tearDown() {
        bookingRepository.deleteAll();
        roomRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @WithMockUser(username = "test@test.com")
    void createBookingTest() {

        Room room = createRoomForTesting();

        BookingRequest request = new BookingRequest();
        request.setRoomId(room.getId());
        request.setCheckInDate(LocalDate.of(2025, 1,1));
        request.setCheckOutDate(LocalDate.of(2025, 1,3));

        BookingResponse response = bookingService.createBooking(request);
        Assertions.assertNotNull(response);
        Assertions.assertEquals("Successfully created booking", response.getMessage());
        Assertions.assertEquals(room.getRoomType(), response.getRoomType());
        Assertions.assertNotNull(response.getBookingId());

    }

    @Test
    @WithMockUser(username = "test@test.com")
    void CreateBookingTest_RoomNotAvailable() {
        User user = createUserForTesting();

        Room room = createRoomForTesting();

        Booking existingBooking = new Booking();
        existingBooking.setRoom(room);
        existingBooking.setUser(user);
        existingBooking.setCheckInDate(LocalDate.now().plusDays(1));
        existingBooking.setCheckOutDate(LocalDate.now().plusDays(3));
        bookingRepository.save(existingBooking);

        BookingRequest request = new BookingRequest();
        request.setRoomId(room.getId());
        request.setCheckInDate(LocalDate.now().plusDays(2));
        request.setCheckOutDate(LocalDate.now().plusDays(4));

        ValidationException exception = Assertions.assertThrows(ValidationException.class, () -> bookingService.createBooking(request));
        Assertions.assertEquals("Room is not available for the selected dates", exception.getMessage());
    }

    @Test
    void getBookingTest() {
        Room room = createRoomForTesting();
        Booking booking = createBooking(
                LocalDate.of(2025, 1, 3),
                LocalDate.of(2025,1,5), room);
        booking = bookingRepository.save(booking);
        BookingResponse response = bookingService.getBooking(booking.getId());

        Assertions.assertEquals(booking.getRoom().getRoomType(), response.getRoomType());
        Assertions.assertEquals(booking.getId(), response.getBookingId());

    }

    @Test
    void getBookingTest_BookingNotFound() {

        EntityNotFoundException entityNotFoundException = Assertions.assertThrows(EntityNotFoundException.class, () -> bookingService.getBooking(1L));
        Assertions.assertEquals("Booking with id: 1 not found", entityNotFoundException.getMessage());

    }

    @Test
    @WithMockUser(username = "test@test.com")
    void updateBookingTest() {
        User user = createUserForTesting();

        Room room = createRoomForTesting();


        Booking existingBooking1 = createBookingForTesting(room, user,
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(3));
        bookingRepository.save(existingBooking1);

        Booking existingBooking2 = createBookingForTesting(room, user,
                LocalDate.now().plusDays(5),
                LocalDate.now().plusDays(7));
        bookingRepository.save(existingBooking2);

        BookingRequest request = new BookingRequest(
                room.getId(),
                LocalDate.now().plusDays(8),
                LocalDate.now().plusDays(9));

        BookingResponse updateBooking = bookingService.updateBooking(existingBooking1.getId(), request);
        Assertions.assertEquals(LocalDate.now().plusDays(8), updateBooking.getCheckInDate());
        Assertions.assertEquals(LocalDate.now().plusDays(9), updateBooking.getCheckOutDate());

        BookingRequest request2 = new BookingRequest(
                room.getId(),
                LocalDate.now().plusDays(5),
                LocalDate.now().plusDays(9));
        Assertions.assertThrows(ValidationException.class, () -> bookingService.updateBooking(existingBooking2.getId(), request2));

    }

    @Test
    @WithMockUser(username = "test@test.com")
    void deleteBookingTest() {
        User user = createUserForTesting();

        Room room = createRoomForTesting();

        Booking booking = createBookingForTesting(room, user, LocalDate.of(2025, 1, 3), LocalDate.of(2025, 1, 5));
        bookingService.deleteBooking(booking.getId());
        Assertions.assertEquals(BookingStatus.DELETED, bookingRepository.findById(booking.getId()).orElseThrow().getStatus());
    }

    @Test
    void getAllBookingsTest() {
        Room room = createRoomForTesting();
        User user = createUserForTesting();
        createBookingForTesting(room, user, LocalDate.of(2025, 1, 3), LocalDate.of(2025, 1, 5));
        createBookingForTesting(room, user, LocalDate.of(2025, 1, 6), LocalDate.of(2025, 1, 7));

        List<BookingResponse> bookings = bookingService.getAllBookings();
        Assertions.assertEquals(2, bookings.size());
    }


    private Booking createBookingForTesting(Room room, User user, LocalDate checkInDate, LocalDate checkOutDate) {
        Booking existingBooking = new Booking();
        existingBooking.setRoom(room);
        existingBooking.setUser(user);
        existingBooking.setCheckInDate(checkInDate);
        existingBooking.setCheckOutDate(checkOutDate);
        return bookingRepository.save(existingBooking);
    }


    public Room createRoomForTesting() {
        Room room = new Room();
        room.setRoomType(RoomType.DOUBLE_STANDARD);
        room.setRoomNumber(3);
        room = roomRepository.save(room);
        return room;
    }

    User createUserForTesting() {
        User user = new User();
        user.setEmail("test@test.com");
        user.setFirstName("Test");
        user.setLastName("Test");
        user.setPassword("test");
        user.setPhoneNumber(89211111111L);
        user.setDateOfBirth(LocalDate.of(1995,11,1));
        user.setStatus(UserStatus.CREATED);
        return userRepository.save(user);
    }

}
