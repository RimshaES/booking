package com.rimsha;

import com.rimsha.model.db.entity.Booking;
import com.rimsha.model.db.entity.Room;
import com.rimsha.model.db.entity.Service;
import com.rimsha.model.db.entity.User;
import com.rimsha.model.db.repository.BookingRepository;
import com.rimsha.model.db.repository.RoomRepository;
import com.rimsha.model.db.repository.UserRepository;
import com.rimsha.model.enums.RoomType;
import com.rimsha.model.enums.ServiceType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.List;

import static org.junit.Assert.assertNotNull;

public class RepositoryTest extends DbTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoomRepository roomRepository;
    @Autowired
    private BookingRepository bookingRepository;

    @Test
    void bookingTest() {
        User userTest = createUser();

        Assertions.assertNotNull(userTest.getId());

        Room room = createRoom();
        Service cleaning = createService(ServiceType.CLEANING, 0.0);
        Service breakfast = createService(ServiceType.BREAKFAST, 500.0);
        Service parking = createService(ServiceType.PARKING, 400.0);

        List<Service> services = List.of(cleaning, breakfast, parking);
        
        Booking reservation = new Booking();
        reservation.setRoom(room);
        reservation.setServices(services);
        reservation.setDateStart(LocalDate.of(2024, 11,1));
        reservation.setDateEnd(LocalDate.of(2024, 11,5));

        Booking bookingTest = bookingRepository.save(reservation);
        Assertions.assertNotNull(bookingTest.getId());

    }

    private User createUser() {
        User user = new User();
        user.setFirstName("Ann");
        user.setLastName("Li");
        user.setEmail("test@test.com");
        user.setPassword("password");
        user.setPhoneNumber(89211112233L);
        user.setDateOfBirth(LocalDate.of(1990, 10, 1));
        User userTest = userRepository.save(user);
        return userTest;
    }

    private Room createRoom() {
        Room room = new Room();
        room.setRoomType(RoomType.SINGLE_COMFORT);
        room.setCoast(4000.0);
        room.setDescription("Одноместный номер");

        Room roomTest = roomRepository.save(room);
        return roomTest;
    }

    private Service createService(ServiceType serviceType, Double coast) {
        Service service = new Service();

        service.setServiceType(serviceType);
        service.setCoast(coast);

        return service;
    }
}
