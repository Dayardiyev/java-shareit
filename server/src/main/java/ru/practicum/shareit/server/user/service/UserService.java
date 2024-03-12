package ru.practicum.shareit.server.user.service;

import ru.practicum.shareit.server.user.model.User;

import java.util.List;

public interface UserService {
    List<User> findAll(int from, int size);

    User findById(long id);

    User create(User user);

    User update(long id, User user);

    void delete(long id);
}
