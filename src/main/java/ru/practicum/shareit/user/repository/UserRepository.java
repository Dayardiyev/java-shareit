package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    User create(User user);

    List<User> findAll();

    User findById(long id);

    User update(long userId, User user);

    void delete(long userId);

    Optional<User> findByEmail(String email);
}