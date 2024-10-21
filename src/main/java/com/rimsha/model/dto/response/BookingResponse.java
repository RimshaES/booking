package com.rimsha.model.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.rimsha.model.enums.RoomType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class BookingResponse {

    String message;
    RoomType roomType;
    LocalDate checkInDate;
    LocalDate checkOutDate;
    Long bookingId;
}
