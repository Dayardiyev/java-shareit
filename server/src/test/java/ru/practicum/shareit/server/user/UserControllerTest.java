package ru.practicum.shareit.server.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.server.common.ObjectGenerator;
import ru.practicum.shareit.server.common.exception.NotFoundException;
import ru.practicum.shareit.server.user.controller.UserController;
import ru.practicum.shareit.server.user.dto.UserCreateRequest;
import ru.practicum.shareit.server.user.dto.UserMapper;
import ru.practicum.shareit.server.user.dto.UserResponse;
import ru.practicum.shareit.server.user.dto.UserUpdateRequest;
import ru.practicum.shareit.server.user.model.User;
import ru.practicum.shareit.server.user.service.UserService;

import java.util.List;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    private final ObjectGenerator objectGenerator = new ObjectGenerator();

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController controller;

    @Spy
    private UserMapper mapper = Mappers.getMapper(UserMapper.class);

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

    @Test
    void getAll_whenUsersExist_thenListOfTwoUsersReturned() {
        when(userService.findAll(0, 50))
                .thenReturn(List.of(user1, user2));

        List<UserResponse> response = controller.findAll(0, 50);

        assertThat(response, hasSize(2));
        assertThat(response.get(0).getName(), equalTo(user1.getName()));
        assertThat(response.get(1).getName(), equalTo(user2.getName()));
        verify(userService).findAll(0, 50);
    }

    @Test
    void getById_whenUserExists_thenUserReturned() {
        long userId = 1L;
        when(userService.findById(userId))
                .thenReturn(user1);

        UserResponse response = controller.findById(userId);

        assertThat(response.getName(), equalTo(user1.getName()));
        verify(userService).findById(userId);
    }

    @Test
    void getById_whenUserNotExists_thenNotFoundReturned() {
        long userId = 0L;
        String errorMessage = "Объект не найден.";
        when(userService.findById(userId))
                .thenThrow(new NotFoundException(errorMessage));

        assertThrows(NotFoundException.class, () -> controller.findById(userId));
        verify(userService).findById(userId);
    }

    @Test
    void create_whenValidUserCreateRequest_thenCreatedUserReturned() {
        long newUserId = 1L;
        when(userService.create(any()))
                .thenAnswer(invocationOnMock -> {
                    User createdUser = invocationOnMock.getArgument(0, User.class);
                    createdUser.setId(newUserId);
                    return createdUser;
                });

        UserResponse response = controller.create(userCreateRequest1);

        assertThat(response.getName(), equalTo(userCreateRequest1.getName()));
        verify(userService).create(any());
    }

    @Test
    void update_whenValidUserUpdateRequest_thenOkReturned() {
        long userId = 1L;
        when(userService.update(anyLong(), any()))
                .thenAnswer(invocationOnMock -> {
                    User updatedUser = invocationOnMock.getArgument(1, User.class);
                    updatedUser.setName(userUpdateRequest1.getName());
                    updatedUser.setEmail(userUpdateRequest1.getEmail());
                    return updatedUser;
                });

        UserResponse response = controller.update(userId, userUpdateRequest1);

        assertThat(response.getName(), equalTo(userUpdateRequest1.getName()));
        assertThat(response.getEmail(), equalTo(userUpdateRequest1.getEmail()));
        verify(userService).update(anyLong(), any());
    }

    @Test
    void delete_whenUserExistsOrNot_thenOkReturned() {
        long userId = 1L;

        controller.delete(userId);

        verify(userService).delete(userId);
    }
}