package ru.practicum.shareit.user.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.common.exception.NotFoundException;
import ru.practicum.shareit.user.dto.*;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Service
@Getter
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository repository;
    private final UserMapper mapper;

    @Override
    public List<User> findAll(int from, int size) {
        return repository.findAll(PageRequest.of(from / size, size))
                .toList();
    }

    @Override
    public User findById(long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + id + " не найден"));
    }

    @Override
    public User create(User user) {
        return repository.save(user);
    }

    @Override
    public User update(long id, User user) {
        User savedUser = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + id + " не найден!"));

        mapper.merge(savedUser, user);
        return repository.save(savedUser);
    }

    @Override
    public void delete(long id) {
        repository.deleteById(id);
    }
}
