package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.common.exception.NotFoundException;
import ru.practicum.shareit.common.ObjectGenerator;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Import(ObjectGenerator.class)
@AutoConfigureTestDatabase
@Transactional
@Rollback
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserServiceIT {

    private final UserService userService;
    private final ObjectGenerator objectGenerator;

    private User user1;
    private User user2;

    @BeforeEach
    void setUp() {
        user1 = objectGenerator.next(User.class);
        user2 = objectGenerator.next(User.class);
    }

    @Test
    void getAll_whenUsersExist_thenListOfTwoUsersReturned() {
        userService.create(user1);
        userService.create(user2);

        List<User> actualUsers = userService.findAll(0, 50);

        assertThat(actualUsers, contains(user1, user2));
    }

    @Test
    void getAll_whenUsersNotExist_thenEmptyListReturned() {
        List<User> actualUsers = userService.findAll(0, 50);

        assertThat(actualUsers, hasSize(0));
    }

    @Test
    void getById_whenUserExists_thenUserReturned() {
        user1 = userService.create(user1);
        user2 = userService.create(user2);

        User actual = userService.findById(user1.getId());

        assertThat(actual, equalTo(user1));
    }

    @Test
    void getById_whenUserNotExists_thenEntityNotFoundExceptionThrown() {
        long userId = 0L;

        assertThrows(NotFoundException.class, () -> userService.findById(userId));
    }

    @Test
    void create_whenUserExistsAndCreateWithEmailDuplicate_thenDataIntegrityViolationExceptionThrown() {
        user1.setEmail("same@mail.com");
        user2.setEmail("same@mail.com");

        userService.create(user1);

        assertThrows(DataIntegrityViolationException.class, () -> userService.create(user2));
    }

    @Test
    void update_whenUserExists_thenUpdatedUserReturned() {
        User updateUser = new User();
        updateUser.setName("Updated name");

        user1 = userService.create(user1);
        user1 = userService.update(user1.getId(), updateUser);

        assertThat(user1.getName(), equalTo(updateUser.getName()));
    }

    @Test
    void delete_whenUserExists_thenUserDeleted() {
        user1 = userService.create(user1);

        userService.delete(user1.getId());
        assertThrows(NotFoundException.class, () -> userService.findById(user1.getId()));
    }
}
