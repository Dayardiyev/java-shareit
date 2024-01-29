package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.model.User;

import javax.validation.Valid;
import java.util.List;

public interface UserService {
    List<User> findAll();

    User findById(long id);

    User create(@Valid User user);

    User update(long id, @Valid User user);

    void delete(long id);
}
