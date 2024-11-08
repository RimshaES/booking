package com.rimsha.model.db.repository;

import com.rimsha.model.db.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {

    @Query("SELECT r FROM Room r WHERE r.id NOT IN (" +
            "SELECT b.room.id FROM Booking b WHERE " +
            "(b.checkInDate < :checkOutDate AND b.checkOutDate > :checkInDate)) " +
            "AND r.maxCapacity >= :personQty")
    List<Room> findAvailableRooms(@Param("checkInDate") LocalDate checkInDate,
                                  @Param("checkOutDate") LocalDate checkOutDate,
                                  @Param("personQty") Integer personQty);

    Optional<Room> findByRoomNumber(Integer roomNumber);
}
