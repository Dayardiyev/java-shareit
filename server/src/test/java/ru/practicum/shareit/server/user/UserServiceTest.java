package ru.practicum.shareit.server.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.server.common.ObjectGenerator;
import ru.practicum.shareit.server.common.exception.NotFoundException;
import ru.practicum.shareit.server.user.dto.UserMapper;
import ru.practicum.shareit.server.user.model.User;
import ru.practicum.shareit.server.user.repository.UserRepository;
import ru.practicum.shareit.server.user.service.UserServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Spy
    private UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    @InjectMocks
    private UserServiceImpl userService;

    private final ObjectGenerator objectGenerator = new ObjectGenerator();

    private User user1;
    private User user2;

    @BeforeEach
    void setUp() {
        user1 = objectGenerator.next(User.class);
        user2 = objectGenerator.next(User.class);

        user1.setId(1L);
        user2.setId(2L);
    }

    @Test
    void findAll_whenUsersExist_thenNonEmptyListReturned() {
        when(userRepository.findAll(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(user1, user2)));

        List<User> actualUsers = userService.findAll(0, 50);

        assertThat(actualUsers, hasSize(2));
        verify(userRepository).findAll(any(Pageable.class));
    }

    @Test
    void findAll_whenUsersNotExist_thenEmptyListReturned() {
        when(userRepository.findAll(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        List<User> actualUsers = userService.findAll(0, 50);

        assertThat(actualUsers, hasSize(0));
        verify(userRepository).findAll(any(Pageable.class));
    }

    @Test
    void findById_whenUserExists_thenUserReturned() {
        long userId = 1L;
        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user1));

        User actualUser = userService.findById(userId);

        assertThat(actualUser, is(user1));
        verify(userRepository).findById(userId);
    }

    @Test
    void findById_whenUserNotExists_thenEntityNotFoundExceptionThrown() {
        long userId = 1L;
        when(userRepository.findById(userId))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.findById(userId));
        verify(userRepository).findById(userId);
    }

    @Test
    void create_whenProvidedNewUser_thenCreatedUserReturned() {
        userService.create(user1);

        verify(userRepository).save(user1);
    }

    @Test
    void update_whenUserNotExists_thenEntityNotFoundExceptionReturned() {
        long userId = 1L;
        User updateUser = new User();

        when(userRepository.findById(userId))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.update(userId, updateUser));
        verify(userRepository).findById(userId);
        verify(userMapper, never()).merge(any(), any());
        verify(userRepository, never()).save(any());
    }

    @Test
    void update_whenUserExistsAndUpdateOnlyName_thenUpdatedUserReturned() {
        long userId = user1.getId();
        User updateUser = new User();
        updateUser.setName("Updated name");

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user1));
        when(userRepository.save(user1))
                .thenReturn(user1);

        userService.update(userId, updateUser);

        assertThat(user1.getName(), equalTo(updateUser.getName()));
        assertThat(user1.getEmail(), not(equalTo(updateUser.getEmail())));

        InOrder inOrder = inOrder(userRepository, userMapper);
        inOrder.verify(userRepository).findById(userId);
        inOrder.verify(userMapper).merge(user1, updateUser);
        inOrder.verify(userRepository).save(user1);
    }

    @Test
    void update_whenUserExistsAndUpdateOnlyEmail_thenUpdatedUserReturned() {
        long userId = user1.getId();
        User updateUser = new User();
        updateUser.setEmail("updated_name@mail.com");

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user1));
        when(userRepository.save(user1))
                .thenReturn(user1);

        user1 = userService.update(userId, updateUser);

        assertThat(user1.getName(), not(equalTo(updateUser.getName())));
        assertThat(user1.getEmail(), equalTo(updateUser.getEmail()));

        InOrder inOrder = inOrder(userRepository, userMapper);
        inOrder.verify(userRepository).findById(userId);
        inOrder.verify(userMapper).merge(user1, updateUser);
        inOrder.verify(userRepository).save(user1);
    }

    @Test
    void delete_whenUserExistsOrNot_thenNothingReturned() {
        long userId = 1L;

        userService.delete(userId);

        verify(userRepository).deleteById(userId);
    }
}