package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.common.exception.NotFoundException;
import ru.practicum.shareit.user.dto.*;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository repository;
    private final UserMapper mapper;

    @Override
    public List<UserResponse> findAll() {
        return mapper.mapToResponseEntity(repository.findAll());
    }

    @Override
    public UserResponse findById(long id) {
        User user = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + id + " не найден"));
        return mapper.mapToResponseEntity(user);
    }

    @Override
    public UserResponse create(UserCreateRequest userCreateRequest) {
        User user = mapper.mapFromCreateRequestDto(userCreateRequest);
        return mapper.mapToResponseEntity(repository.save(user));
    }

    @Override
    public UserResponse update(long id, UserUpdateRequest userWithUpdatedParameters) {
        User savedUser = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + id + " не найден!"));

        User mappedUserFromUpdated = mapper.mapFromUpdateRequestDto(userWithUpdatedParameters);
        mapper.merge(savedUser, mappedUserFromUpdated);

        return mapper.mapToResponseEntity(repository.save(savedUser));
    }

    @Override
    public void delete(long id) {
        repository.deleteById(id);
    }
}
