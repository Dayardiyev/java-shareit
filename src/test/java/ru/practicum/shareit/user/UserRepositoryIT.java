package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import ru.practicum.shareit.common.ObjectGenerator;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import(ObjectGenerator.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserRepositoryIT {

    private final UserRepository userRepository;
    private final ObjectGenerator objectGenerator;

    private User user1;
    private User user2;

    @BeforeEach
    public void setUp() {
        user1 = objectGenerator.next(User.class);
        user2 = objectGenerator.next(User.class);
    }

    @Test
    public void findByEmail_whenUserNotFound_thenEmptyOptionalReturned() {
        String wrongEmail = "wrong@email.com";
        Optional<User> optional = userRepository.findByEmail(wrongEmail);

        assertTrue(optional.isEmpty());
    }

    @Test
    public void findByEmail_whenUserFound_thenPresentOptionalReturned() {
        userRepository.save(user1);
        userRepository.save(user2);

        User foundUser = userRepository.findByEmail(user1.getEmail())
                .orElseThrow();

        assertThat(foundUser, equalTo(user1));
    }

    @Test
    public void save_whenEmailIsNull_thenExceptionThrown() {
        user1.setEmail(null);

        assertThrows(DataIntegrityViolationException.class, () -> userRepository.save(user1));
    }

    @Test
    public void save_whenNameIsNull_thenExceptionThrown() {
        user1.setName(null);

        assertThrows(DataIntegrityViolationException.class, () -> userRepository.save(user1));
    }

    @Test
    public void save_whenDuplicateEmail_thenExceptionThrown() {
        String email = "duplicate@email.com";
        user1.setEmail(email);
        user2.setEmail(email);

        userRepository.save(user1);

        assertThrows(DataIntegrityViolationException.class, () -> userRepository.save(user2));
    }
}
