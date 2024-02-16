package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidateException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository repository;

    @Override
    public List<User> findAll() {
        return repository.findAll();
    }

    @Override
    public User findById(long id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException("Пользователь с id=" + id + " не найден"));
    }

    @Override
    public void delete(long id) {
        repository.delete(id);
    }

    @Override
    public User create(User user) {
        isAlreadyExistsEmail(user.getEmail());
        return repository.create(user);
    }

    @Override
    public User update(long id, User user) {
        if (user.getEmail() != null) {
            validateUniqueEmail(id, user.getEmail());
        }

        Optional<User> optionalUser = repository.findById(id);
        User previousUser = optionalUser
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с id=%d не найден!", id)));

        User updatedUser = User.builder()
                .id(id)
                .name(Objects.requireNonNullElse(user.getName(), previousUser.getName()))
                .email(Objects.requireNonNullElse(user.getEmail(), previousUser.getEmail()))
                .build();

        return repository.update(id, updatedUser);
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
