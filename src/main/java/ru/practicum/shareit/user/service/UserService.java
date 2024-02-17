package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserCreateRequest;
import ru.practicum.shareit.user.dto.UserResponse;
import ru.practicum.shareit.user.dto.UserUpdateRequest;

import java.util.List;

public interface UserService {
    List<UserResponse> findAll();

    UserResponse findById(long id);

    UserResponse create(UserCreateRequest userCreateRequest);

    UserResponse update(long id, UserUpdateRequest userUpdateRequest);

    void delete(long id);
}
