package ru.practicum.shareit.user.repository;


import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Repository
public class UserRepositoryImpl implements UserRepository {
    private final Map<Long, User> users;

    private static long serial = 1;

    public UserRepositoryImpl() {
        users = new HashMap<>();
    }

    @Override
    public User update(long userId, User user) {
        users.put(userId, user);
        return user;
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return users.values().stream()
                .filter(u -> email.equals(u.getEmail()))
                .findFirst();
    }

    @Override
    public void delete(long userId) {
        users.remove(userId);
    }

    @Override
    public User create(User user) {
        long id = getUniqueId();
        user.setId(id);
        users.put(id, user);
        return user;
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public Optional<User> findById(long userId) {
        return Optional.ofNullable(users.get(userId));
    }

    private long getUniqueId() {
        return serial++;
    }
}
