package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.common.exception.NotFoundException;
import ru.practicum.shareit.common.ObjectGenerator;
import ru.practicum.shareit.user.controller.UserController;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.dto.UserCreateRequest;
import ru.practicum.shareit.user.dto.UserUpdateRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({UserController.class, UserMapper.class, ObjectGenerator.class})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserControllerIT {

    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    private final ObjectGenerator objectGenerator;

    @MockBean
    private UserService userService;

    private UserCreateRequest userCreateRequest1;
    private UserUpdateRequest userUpdateRequest1;

    private User user1;
    private User user2;

    @BeforeEach
    void setUp() {
        user1 = objectGenerator.next(User.class);
        user2 = objectGenerator.next(User.class);

        user1.setId(1L);
        user2.setId(2L);

        userCreateRequest1 = objectGenerator.next(UserCreateRequest.class);
        userUpdateRequest1 = objectGenerator.next(UserUpdateRequest.class);
    }

    @SneakyThrows
    @Test
    void getAll_whenUsersExist_thenListOfTwoUsersReturned() {
        when(userService.findAll(0, 50))
                .thenReturn(List.of(user1, user2));

        mockMvc.perform(get("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(user1.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(user1.getName())))
                .andExpect(jsonPath("$[0].email", is(user1.getEmail())))
                .andExpect(jsonPath("$[1].id", is(user2.getId()), Long.class))
                .andExpect(jsonPath("$[1].name", is(user2.getName())))
                .andExpect(jsonPath("$[1].email", is(user2.getEmail()))
                );

        verify(userService).findAll(0, 50);
    }

    @SneakyThrows
    @Test
    void getById_whenUserExists_thenUserReturned() {
        long userId = 1L;
        when(userService.findById(userId))
                .thenReturn(user1);

        mockMvc.perform(get("/users/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(user1.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(user1.getName())))
                .andExpect(jsonPath("$.email", is(user1.getEmail())));

        verify(userService).findById(userId);
    }

    @SneakyThrows
    @Test
    void getById_whenUserNotExists_thenNotFoundReturned() {
        long userId = 0L;
        String errorMessage = "Объект не найден.";

        when(userService.findById(userId))
                .thenThrow(new NotFoundException(errorMessage));

        mockMvc.perform(get("/users/{id}", userId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", Matchers.containsString(errorMessage)));

        verify(userService).findById(userId);
    }

    @SneakyThrows
    @Test
    void create_whenValidUserCreateRequest_thenCreatedUserReturned() {
        long newUserId = 1L;
        when(userService.create(any()))
                .thenAnswer(invocationOnMock -> {
                    User createdUser = invocationOnMock.getArgument(0, User.class);
                    createdUser.setId(newUserId);
                    return createdUser;
                });

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(userCreateRequest1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(newUserId), Long.class))
                .andExpect(jsonPath("$.name", is(userCreateRequest1.getName())))
                .andExpect(jsonPath("$.email", is(userCreateRequest1.getEmail())));

        verify(userService).create(any());
    }

    @SneakyThrows
    @Test
    void create_whenUserNameIsNull_thenBadRequestReturned() {
        String errorMessage = "Имя пользователя не может быть пустым.";
        userCreateRequest1.setName(null);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(userCreateRequest1)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", Matchers.containsString(errorMessage)));

        verify(userService, never()).create(any());
    }

    @SneakyThrows
    @Test
    void create_whenUserNameIsBlank_thenBadRequestReturned() {
        String errorMessage = "Имя пользователя не может быть пустым.";
        userCreateRequest1.setName(" ");

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(userCreateRequest1)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", Matchers.containsString(errorMessage)));

        verify(userService, never()).create(any());
    }

    @SneakyThrows
    @Test
    void create_whenEmailIsNull_thenBadRequestReturned() {
        String errorMessage = "Электронная почта не может быть пустым.";
        userCreateRequest1.setEmail(null);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(userCreateRequest1)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", Matchers.containsString(errorMessage)));

        verify(userService, never()).create(any());
    }

    @SneakyThrows
    @Test
    void create_whenEmailIsBlank_thenBadRequestReturned() {
        String errorMessage = "Электронная почта не может быть пустым.";
        userCreateRequest1.setEmail(" ");

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(userCreateRequest1)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", Matchers.containsString(errorMessage)));

        verify(userService, never()).create(any());
    }

    @SneakyThrows
    @Test
    void create_whenEmailAlreadyUsed_thenBadRequestReturned() {
        String errorMessage = "Электронный адрес уже используется.";

        when(userService.create(any()))
                .thenThrow(new DataIntegrityViolationException("Электронный адрес уже используется."));

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(userCreateRequest1)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error", Matchers.containsString(errorMessage)));

        verify(userService).create(any());
    }

    @SneakyThrows
    @Test
    void create_whenEmailInvalid_thenBadRequestReturned() {
        String errorMessage = "Электронная почта не соответствует формату \"user@mail.com\".";
        userCreateRequest1.setEmail("incorrect email format@");

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(userCreateRequest1)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", Matchers.containsString(errorMessage)));

        verify(userService, never()).create(any());
    }

    @SneakyThrows
    @Test
    void update_whenValidUserUpdateRequest_thenOkReturned() {
        long userId = 1L;

        mockMvc.perform(patch("/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(userUpdateRequest1)))
                .andExpect(status().isOk());

        verify(userService).update(anyLong(), any());
    }

    @SneakyThrows
    @Test
    void update_whenInvalidEmail_thenBadRequestReturned() {
        String errorMessage = "Электронная почта не соответствует формату \"user@mail.com\".";
        long userId = 1L;
        userUpdateRequest1.setEmail("invalid email format@.com");

        mockMvc.perform(patch("/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(userUpdateRequest1)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", Matchers.containsString(errorMessage)));

        verify(userService, never()).update(anyLong(), any());
    }

    @SneakyThrows
    @Test
    void delete_whenUserExistsOrNot_thenOkReturned() {
        long userId = 1L;

        mockMvc.perform(delete("/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk());

        verify(userService).delete(userId);
    }
}