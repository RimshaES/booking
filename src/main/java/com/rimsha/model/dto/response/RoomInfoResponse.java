package com.rimsha.model.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.rimsha.model.dto.request.RoomInfoRequest;
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
public class RoomInfoResponse extends RoomInfoRequest {
    Long id;
}
