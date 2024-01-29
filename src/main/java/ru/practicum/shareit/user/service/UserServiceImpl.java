package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.exception.FieldNotFilledException;
import ru.practicum.shareit.exception.ValidateException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.validation.*;
import java.util.List;
import java.util.Optional;

@Service
@Validated
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository repository;

    @Override
    public List<User> findAll() {
        return repository.findAll();
    }

    @Override
    public User findById(long id) {
        return repository.findById(id);
    }

    @Override
    public void delete(long id) {
        repository.delete(id);
    }

    @Override
    public User create(@Valid User user) {
        if (user.getEmail() == null) {
            throw new FieldNotFilledException("Почта не заполнена");
        }
        isAlreadyExistsEmail(user.getEmail());
        return repository.create(user);
    }

    @Override
    public User update(long id, @Valid User user) {
        if (user.getEmail() != null) {
            validateUniqueEmail(id, user.getEmail());
        }
        return repository.update(id, user);
    }

    private void validateUniqueEmail(long id, String email) {
        User existingUser = findById(id);
        if (!existingUser.getEmail().equals(email)) {
            isAlreadyExistsEmail(email);
        }
    }

    private void isAlreadyExistsEmail(String email) {
        Optional<User> optional = repository.findByEmail(email);
        if (optional.isPresent()) {
            throw new ValidateException("Пользователь с такой почтой уже существует");
        }
    }
}
