package com.rimsha.Controllers;

import com.rimsha.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.rimsha.constants.Constants.BOOKINGS;
import static com.rimsha.constants.Constants.USERS;

@RestController
@RequestMapping(BOOKINGS)
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;


}
