package ru.practicum.shareit.user.repository;


import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Repository
public class UserRepositoryImpl implements UserRepository {
    private final Map<Long, User> users;

    private static long serial = 1;

    public UserRepositoryImpl() {
        users = new HashMap<>();
    }

    public User update(long userId, User user) {
        User previousUser = users.get(userId);
        if (previousUser == null) {
            throw new NotFoundException(String.format("Пользователь с id=%d не найден!", userId));
        }

        User updatedUser = User.builder()
                .id(userId)
                .name(Objects.requireNonNullElse(user.getName(), previousUser.getName()))
                .email(Objects.requireNonNullElse(user.getEmail(), previousUser.getEmail()))
                .build();

        users.put(userId, updatedUser);
        return updatedUser;
    }


    public Optional<User> findByEmail(String email) {
        return users.values().stream()
                .filter(u -> email.equals(u.getEmail()))
                .findFirst();
    }

    @Override
    public void delete(long userId) {
        users.remove(userId);
    }

    public User create(User user) {
        long id = getUniqueId();
        user.setId(id);
        users.put(id, user);
        return user;
    }

    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    public User findById(long userId) {
        return users.get(userId);
    }

    private long getUniqueId() {
        return serial++;
    }
}
