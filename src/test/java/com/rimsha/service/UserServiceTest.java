package com.rimsha.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.rimsha.AbstractTest;
import com.rimsha.exceptions.EntityNotFoundException;
import com.rimsha.exceptions.ValidationException;
import com.rimsha.model.db.entity.User;
import com.rimsha.model.db.repository.UserRepository;
import com.rimsha.model.dto.request.UserInfoRequest;
import com.rimsha.model.dto.response.UserInfoResponse;
import com.rimsha.model.enums.UserStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;

import java.time.LocalDate;
import java.util.List;


public class UserServiceTest extends AbstractTest {

    private UserService userService;

    @Autowired
    UserRepository userRepository;

    private ObjectMapper mapper = new ObjectMapper();

    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @BeforeEach
    void setUp() {
        mapper.registerModule(new JavaTimeModule());
        userService = new UserService(mapper, userRepository, passwordEncoder);
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    public void createUserTest() {
        UserInfoRequest request = createUserRequest("test@test.com");

        UserInfoResponse result = userService.createUser(request);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(request.getEmail(), result.getEmail());
        Assertions.assertEquals(request.getFirstName(), result.getFirstName());
        Assertions.assertEquals(request.getLastName(), result.getLastName());
        Assertions.assertEquals(request.getDateOfBirth(), result.getDateOfBirth());
    }

    private static UserInfoRequest createUserRequest(String email) {
        UserInfoRequest request = new UserInfoRequest();
        request.setEmail(email);
        request.setFirstName("Test");
        request.setLastName("Test");
        request.setPassword("test");
        request.setPhoneNumber(89211111111L);
        request.setDateOfBirth(LocalDate.of(1995,11,1));
        return request;
    }

    @Test
    public void testCreateUser_userExists() {
        UserInfoRequest request = createUserRequest("test@test.com");
        userService.createUser(request);

        UserInfoRequest request1 = createUserRequest("test@test.com");

        Assertions.assertThrows(ValidationException.class, () -> userService.createUser(request1));
    }

    @Test
    @WithMockUser(username = "test@test.com")
    public void testGetUser() {
        UserInfoRequest request = createUserRequest("test@test.com");
        UserInfoResponse result = userService.createUser(request);
        UserInfoResponse user = userService.getUser(result.getId());
        Assertions.assertEquals(request.getEmail(), user.getEmail());
    }

    @Test
    @WithMockUser(username = "test7@test.com")
    public void testGetUser_NotPrincipal() {
        UserInfoRequest request = createUserRequest("test@test.com");
        UserInfoResponse result = userService.createUser(request);
        Assertions.assertThrows(ValidationException.class, () -> userService.getUser(result.getId()));
    }

    @Test
    @WithMockUser(username = "test@test.com")
    public void testGetUser_NotFound() {
        Assertions.assertThrows(EntityNotFoundException.class, () -> userService.getUser(123L));
    }

    @Test
    @WithMockUser(username = "test@test.com", roles = {"ADMIN"})
    public void testUpdateUser() {
        User user = createUser("test@test.com");

        UserInfoRequest updateRequest = new UserInfoRequest();
        updateRequest.setEmail("test@test.com");
        updateRequest.setFirstName("Ivan");
        updateRequest.setLastName("Sokolov");
        updateRequest.setDateOfBirth(LocalDate.of(1985,11,1));
        updateRequest.setPhoneNumber(89211111111L);
        updateRequest.setPassword("test");

        UserInfoResponse updatedResult = userService.updateUser(user.getId(), updateRequest);
        Assertions.assertNotNull(updatedResult);
        Assertions.assertEquals(updateRequest.getEmail(), updatedResult.getEmail());
        Assertions.assertEquals(updateRequest.getFirstName(), updatedResult.getFirstName());
        Assertions.assertEquals(updateRequest.getLastName(), updatedResult.getLastName());
        Assertions.assertEquals(updateRequest.getPhoneNumber(), updatedResult.getPhoneNumber());
        Assertions.assertEquals(updateRequest.getDateOfBirth(), updatedResult.getDateOfBirth());
    }

    User createUser(String email) {
        User user = new User();
        user.setEmail(email);
        user.setFirstName("Test");
        user.setLastName("Test");
        user.setPassword("test");
        user.setPhoneNumber(89211111111L);
        user.setDateOfBirth(LocalDate.of(1995,11,1));
        user.setStatus(UserStatus.CREATED);
        return userRepository.save(user);
    }

    @Test
    @WithMockUser(username = "random@test.com")
    public void testUpdateUser_otherUser() {
        User user = createUser("test@test.com");

        UserInfoRequest updateRequest = new UserInfoRequest();
        updateRequest.setEmail("test@test.com");
        updateRequest.setFirstName("Ivan");
        updateRequest.setLastName("Sokolov");
        updateRequest.setDateOfBirth(LocalDate.of(1985, 11, 1));
        updateRequest.setPhoneNumber(89211111111L);
        updateRequest.setPassword("test");

        Assertions.assertThrows(ValidationException.class, () -> userService.updateUser(user.getId(), updateRequest));

    }

    @Test
    @WithMockUser(username = "random@test.com", roles = {"ADMIN"})
    public void testUpdateUser_OtherUserAdmin() {
        User user = createUser("test@test.com");

        UserInfoRequest updateRequest = new UserInfoRequest();
        updateRequest.setEmail("test@test.com");
        updateRequest.setFirstName("Ivan");
        updateRequest.setLastName("Sokolov");
        updateRequest.setDateOfBirth(LocalDate.of(1985, 11, 1));
        updateRequest.setPhoneNumber(89211111111L);
        updateRequest.setPassword("test");

        UserInfoResponse updatedResult = userService.updateUser(user.getId(), updateRequest);
        Assertions.assertNotNull(updatedResult);
        Assertions.assertEquals(updateRequest.getEmail(), updatedResult.getEmail());

    }

    @Test
    @WithMockUser(username = "test@test.com")
    public void testDeleteUser() {
        User user = createUser("test@test.com");
        userService.deleteUser(user.getId());
        Assertions.assertEquals(UserStatus.DELETED, userRepository.findById(user.getId()).get().getStatus());
    }

    @Test
    @WithMockUser(username = "admin@test.com", roles = {"ADMIN"})
    public void testDeleteUser_OtherUserAdmin() {
        User user = createUser("test@test.com");
        userService.deleteUser(user.getId());
        Assertions.assertEquals(UserStatus.DELETED, userRepository.findById(user.getId()).get().getStatus());
    }

    @Test
    @WithMockUser(username = "random@test.com")
    public void testDeleteUser_OtherUser() {
        User user = createUser("test@test.com");
        Assertions.assertThrows(ValidationException.class, () -> userService.deleteUser(user.getId()));
    }

    @Test
    @WithMockUser(username = "admin@test.com", roles = {"ADMIN"})
    public void testGetAllUsers() {
        createUser("test1@test.com");
        createUser("test2@test.com");

        List<UserInfoResponse> users = userService.getAllUsers();
        Assertions.assertNotNull(users);
        Assertions.assertEquals(2, users.size());
    }

}
