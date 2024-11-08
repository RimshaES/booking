package com.rimsha.model.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.rimsha.model.dto.request.UserInfoRequest;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
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
public class UserInfoResponse {
    Long id;
    String email;
    String firstName;
    String lastName;
    LocalDate dateOfBirth;
    Long phoneNumber;
}
