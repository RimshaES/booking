package com.rimsha.model.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.rimsha.model.enums.RoomType;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class RoomInfoRequest {

    @NotNull
    RoomType roomType;
    @NotNull
    Integer roomNumber;
    @NotNull
    Integer maxCapacity;
    Double coast;
    String description;
}
