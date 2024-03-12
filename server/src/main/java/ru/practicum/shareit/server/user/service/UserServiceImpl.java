package ru.practicum.shareit.server.user.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.server.common.exception.NotFoundException;
import ru.practicum.shareit.server.user.dto.UserMapper;
import ru.practicum.shareit.server.user.repository.UserRepository;
import ru.practicum.shareit.server.user.model.User;

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
