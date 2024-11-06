package com.rimsha.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.rimsha.AbstractTest;
import com.rimsha.exceptions.ValidationException;
import com.rimsha.model.db.entity.Booking;
import com.rimsha.model.db.entity.Room;
import com.rimsha.model.db.repository.BookingRepository;
import com.rimsha.model.db.repository.RoomRepository;
import com.rimsha.model.dto.request.RoomInfoRequest;
import com.rimsha.model.dto.response.RoomInfoResponse;
import com.rimsha.model.enums.RoomStatus;
import com.rimsha.model.enums.RoomType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.List;

class RoomServiceTest extends AbstractTest {

    RoomService roomService;

    @Autowired
    RoomRepository roomRepository;

    @Autowired
    BookingRepository bookingRepository;


    @BeforeEach
    void setUp() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        roomService = new RoomService(mapper, roomRepository);
        preparedTestData();
    }

    @AfterEach
    void tearDown() {
        bookingRepository.deleteAll();
        roomRepository.deleteAll();
    }

    @Test
    void testFindAvailableRooms() {
        List<RoomInfoResponse> list = roomService.findAvailableRooms(LocalDate.of(2024,11,3), LocalDate.of(2024,11,5), 1);

        Assertions.assertEquals(2, list.size());
        Assertions.assertEquals(1, list.get(0).getRoomNumber());
        Assertions.assertEquals(4, list.get(1).getRoomNumber());

        List<RoomInfoResponse> list1 = roomService.findAvailableRooms(LocalDate.of(2024,11,8), LocalDate.of(2024,11,9), 1);

        Assertions.assertEquals(1, list1.size());
        Assertions.assertEquals(2, list1.get(0).getRoomNumber());

        List<RoomInfoResponse> list2 = roomService.findAvailableRooms(LocalDate.of(2024,11,6), LocalDate.of(2024,11,8), 2);

        Assertions.assertEquals(1, list2.size());
        Assertions.assertEquals(3, list2.get(0).getRoomNumber());

    }

    @Test
    void testCreateRoom() {

        RoomInfoRequest request = createRoomInfoRequest(RoomType.SINGLE_STANDARD, 5,1,1000.0, "Single Room");

        RoomInfoResponse response = roomService.createRoom(request);

        Assertions.assertNotNull(response);
        Assertions.assertNotNull(response.getId());
        Assertions.assertEquals(response.getRoomNumber(), request.getRoomNumber());
        Assertions.assertEquals(response.getDescription(), request.getDescription());
        Assertions.assertEquals(response.getMaxCapacity(), request.getMaxCapacity());
        Assertions.assertEquals(response.getCoast(), request.getCoast());
        Assertions.assertEquals(response.getRoomType(), RoomType.SINGLE_STANDARD);
    }

    @Test
    void testCreateRoom_roomExists() {
        RoomInfoRequest request1 = createRoomInfoRequest(RoomType.SINGLE_STANDARD, 2,1,1000.0, "Single Room");

        Assertions.assertThrows(ValidationException.class, () -> roomService.createRoom(request1));
    }

    @Test
    void testGetRoomFromDB () {
        RoomInfoRequest request = createRoomInfoRequest(RoomType.SINGLE_STANDARD, 21,1,1000.0, "Single Room");
        RoomInfoResponse result = roomService.createRoom(request);
        RoomInfoResponse result1 = roomService.getRoom(result.getId());

        Assertions.assertEquals(result1.getRoomNumber(), request.getRoomNumber());
        Assertions.assertEquals(result1.getDescription(), request.getDescription());
    }

    @Test
    void testUpdateRoom() {
        Room room = createRoom(RoomType.DOUBLE_STANDARD, 2000.0, "чайник", 2, 1);
        roomRepository.save(room);
        RoomInfoRequest updateRequest = new RoomInfoRequest();
        updateRequest.setRoomNumber(room.getRoomNumber());
        updateRequest.setDescription(room.getDescription());
        updateRequest.setMaxCapacity(room.getMaxCapacity());
        updateRequest.setCoast(2200.00);
        updateRequest.setRoomType(RoomType.DOUBLE_COMFORT);

        RoomInfoResponse updateResponse = roomService.updateRoom(room.getId(), updateRequest);
        Assertions.assertEquals(updateResponse.getRoomNumber(), room.getRoomNumber());
        Assertions.assertNotNull(updateResponse);
        Assertions.assertEquals(updateRequest.getCoast(), updateResponse.getCoast());
        Assertions.assertEquals(updateRequest.getRoomType(), RoomType.DOUBLE_COMFORT);
        Assertions.assertEquals(updateRequest.getMaxCapacity(), updateResponse.getMaxCapacity());

    }

    @Test
    void testDeleteRoom() {
        Assertions.assertEquals(4, roomRepository.count());
        long idToDelete = roomRepository.findByRoomNumber(1).orElseThrow().getId();
        roomService.deleteRoom(idToDelete);
        Assertions.assertEquals(RoomStatus.DELETED, roomRepository.findById(idToDelete).orElseThrow().getStatus());
    }


    Room createRoom(RoomType roomType, Double coast, String description, Integer capacity, Integer number) {
        Room room = new Room();
        room.setRoomType(roomType);
        room.setCoast(coast);
        room.setDescription(description);
        room.setMaxCapacity(capacity);
        room.setRoomNumber(number);
        return room;
    }

    RoomInfoRequest createRoomInfoRequest(RoomType roomType, Integer roomNumber, Integer maxCapacity, Double coast, String description) {
        RoomInfoRequest request = new RoomInfoRequest();
        request.setRoomType(roomType);
        request.setRoomNumber(roomNumber);
        request.setMaxCapacity(maxCapacity);
        request.setCoast(coast);
        request.setDescription(description);
        return request;
    }

    void preparedTestData() {

        Room room1 = createRoom(RoomType.SINGLE_STANDARD, 1000.0, "чайник", 1, 1);
        Room room2 = createRoom(RoomType.SINGLE_COMFORT, 1500.00, "кофемашина",1, 2);
        Room room3 = createRoom(RoomType.DOUBLE_STANDARD, 2000.00, "чайник",2, 3);
        Room room4 = createRoom(RoomType.DOUBLE_COMFORT, 2500.00, "кофемашина",2, 4);

        roomRepository.saveAll(List.of(room1, room2, room3, room4));

        Booking booking1 = createBooking(LocalDate.of(2024, 11,1), LocalDate.of(2024, 11,3), room1);
        Booking booking2 = createBooking(LocalDate.of(2024, 11,7), LocalDate.of(2024, 11,10), room1);
        Booking booking3 = createBooking(LocalDate.of(2024, 11,2), LocalDate.of(2024, 11,5), room2);
        Booking booking4 = createBooking(LocalDate.of(2024, 11,4), LocalDate.of(2024, 11,6), room3);
        Booking booking5 = createBooking(LocalDate.of(2024, 11,8), LocalDate.of(2024, 11,12), room3);
        Booking booking6 = createBooking(LocalDate.of(2024, 11,1), LocalDate.of(2024, 11,2), room4);
        Booking booking7 = createBooking(LocalDate.of(2024, 11,6), LocalDate.of(2024, 11,9), room4);
        bookingRepository.saveAll(List.of(booking1, booking2, booking3, booking4, booking5, booking6, booking7));

    }
}