package com.rimsha.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rimsha.exceptions.EntityNotFoundException;
import com.rimsha.exceptions.ValidationException;
import com.rimsha.model.db.entity.User;
import com.rimsha.model.db.repository.UserRepository;
import com.rimsha.model.dto.request.UserInfoRequest;
import com.rimsha.model.dto.response.UserInfoResponse;
import com.rimsha.model.enums.UserStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final ObjectMapper mapper;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    public UserInfoResponse createUser(UserInfoRequest request) {

        userRepository.findByEmailIgnoreCase(request.getEmail())
                .ifPresent(user -> {
                    throw new ValidationException(String.format("User with email: %s already exists", request.getEmail()), HttpStatus.BAD_REQUEST);
                });

        User user = mapper.convertValue(request, User.class);
        user.setCreatedAt(LocalDateTime.now());
        user.setStatus(UserStatus.CREATED);
        String password = passwordEncoder.encode(request.getPassword());
        user.setPassword(password);
        User savedUser = userRepository.save(user);
        return mapper.convertValue(savedUser, UserInfoResponse.class);
    }


    private User getUserFromDB(Long id) {
        return userRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.format("User with id: %s not found", id), HttpStatus.NOT_FOUND)
        );
    }

    public UserInfoResponse getUser(Long id) {
        User user = getUserFromDB(id);
        checkIfUserIsPrincipalOrAdmin(user);
        return mapper.convertValue(user, UserInfoResponse.class);
    }

    public UserInfoResponse updateUser(Long id, UserInfoRequest request) {
        User user = getUserFromDB(id);
        checkIfUserIsPrincipalOrAdmin(user);
        user.setEmail(request.getEmail());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setPhoneNumber(request.getPhoneNumber());
        user.setDateOfBirth(request.getDateOfBirth());

        user.setUpdatedAt(LocalDateTime.now());
        user.setStatus(UserStatus.UPDATED);
        User save = userRepository.save(user);
        return mapper.convertValue(save, UserInfoResponse.class);
    }

    public void deleteUser(Long id) {
        User user = getUserFromDB(id);
        checkIfUserIsPrincipalOrAdmin(user);
        user.setUpdatedAt(LocalDateTime.now());
        user.setStatus(UserStatus.DELETED);

        userRepository.save(user);
    }

    public List<UserInfoResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(user -> mapper.convertValue(user, UserInfoResponse.class))
                .collect(Collectors.toList());
    }

    private void checkIfUserIsPrincipalOrAdmin(User user) {
        UserDetails principal = getPrincipal();
        boolean isAdmin = isAdmin(principal);
        if (!principal.getUsername().equals(user.getEmail()) && !isAdmin) {
            throw new ValidationException("No permission for user: " + user.getEmail(), HttpStatus.UNAUTHORIZED);
        }
    }

    public UserDetails getPrincipal() {
        return (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    private boolean isAdmin(UserDetails principal) {
        return principal.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> role.contains("ADMIN"));
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ValidationException(String.format("User with email: %s not found",
                        email), HttpStatus.NOT_FOUND));
    }

}
