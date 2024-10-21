package com.rimsha.Controllers;

import com.rimsha.model.dto.request.BookingRequest;
import com.rimsha.model.dto.response.BookingResponse;
import com.rimsha.service.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static com.rimsha.constants.Constants.BOOKINGS;

@Tag(name = "Бронирования")
@RestController
@RequestMapping(BOOKINGS)
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    @Operation(summary = "Создать бронирование")
    @ResponseStatus(HttpStatus.CREATED)
    public BookingResponse createBooking(@RequestBody @Valid BookingRequest request) {
        return bookingService.createBooking(request);
    }

}
