package com.rimsha.Controllers;

import com.rimsha.model.db.entity.Room;
import com.rimsha.model.dto.request.RoomInfoRequest;
import com.rimsha.model.dto.response.RoomInfoResponse;
import com.rimsha.service.RoomService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import static com.rimsha.constants.Constants.ROOMS;

//@Tag(name = "Пользователи")
@RestController
@RequestMapping(ROOMS)
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;

    @PostMapping
    @Operation(summary = "Создать комнату")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public RoomInfoResponse createRoom(@RequestBody @Valid RoomInfoRequest request) {
        return roomService.createRoom(request);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить комнату по ID")
    public RoomInfoResponse getRoom(@PathVariable Long id) {
        return roomService.getRoom(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновить комнату по ID")
    public RoomInfoResponse updateRoom(@PathVariable Long id, @RequestBody RoomInfoRequest request) {
        return roomService.updateRoom(id, request);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить комнату по ID")
    public void deleteRoom(@PathVariable Long id) {
        roomService.deleteRoom(id);
    }

    @GetMapping("/all")
    @Operation(summary = "Получить список комнат")
    public List<RoomInfoResponse> getAllRooms() {
        return roomService.getAllRooms();
    }

    @GetMapping("/available")
    public ResponseEntity<List<RoomInfoResponse>> getAvailableRooms(@RequestParam LocalDate checkInDate,
                                                                    @RequestParam LocalDate checkOutDate) {
        List<RoomInfoResponse> availableRooms = roomService.findAvailableRooms(checkInDate, checkOutDate);
        return new ResponseEntity<>(availableRooms, HttpStatus.OK);
    }


}
