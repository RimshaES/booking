package com.rimsha.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rimsha.exceptions.EntityNotFoundException;
import com.rimsha.model.db.entity.Room;
import com.rimsha.model.db.repository.RoomRepository;
import com.rimsha.model.dto.request.RoomInfoRequest;
import com.rimsha.model.dto.response.RoomInfoResponse;
import com.rimsha.model.enums.RoomStatus;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoomService {

    private final ObjectMapper mapper;
    private final RoomRepository roomRepository;

    public RoomInfoResponse createRoom(RoomInfoRequest request) {
        Room room = mapper.convertValue(request, Room.class);
        room.setCreatedAt(LocalDateTime.now());
        room.setStatus(RoomStatus.CREATED);
        Room savedRoom = roomRepository.save(room);
        log.info("Room created: {}", savedRoom);
        return mapper.convertValue(savedRoom, RoomInfoResponse.class);
    }

    private Room getRoomFromDB(Long id) {
        return roomRepository.findById(id).orElseThrow();
    }

    public RoomInfoResponse getRoom(Long id) {
        Room room = getRoomFromDB(id);
        return mapper.convertValue(room, RoomInfoResponse.class);
    }

    public RoomInfoResponse updateRoom(Long id, RoomInfoRequest request) {
        Room room = getRoomFromDB(id);

        room.setRoomType(request.getRoomType());
        room.setRoomNumber(request.getRoomNumber());
        room.setDescription(request.getDescription());
        room.setMaxCapacity(request.getMaxCapacity());
        room.setCoast(request.getCoast());

        room.setUpdatedAt(LocalDateTime.now());
        room.setStatus(RoomStatus.UPDATED);

        Room savedRoom = roomRepository.save(room);
        return mapper.convertValue(savedRoom, RoomInfoResponse.class);
    }

    public void deleteRoom(Long id) {
        Room room = getRoomFromDB(id);

        room.setUpdatedAt(LocalDateTime.now());
        room.setStatus(RoomStatus.DELETED);

        roomRepository.save(room);
    }

    public List<RoomInfoResponse> getAllRooms() {
        return roomRepository.findAll().stream()
                .map(room -> mapper.convertValue(room, RoomInfoResponse.class))
                .toList();
    }

    @Transactional
    public List<RoomInfoResponse> findAvailableRooms(LocalDate checkInDate, LocalDate checkOutDate, Integer personQty) {

        List<RoomInfoResponse> list = roomRepository.findAvailableRooms(checkInDate, checkOutDate, personQty)
                .stream().map(room -> mapper. convertValue(room, RoomInfoResponse.class))
                .toList();
        if (list.isEmpty()) {
            throw new EntityNotFoundException(String.format("Rooms for dates %s - %s for %d persons not found", checkInDate, checkOutDate, personQty), HttpStatus.NOT_FOUND);
        }
        return list;
    }
}
