package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping(path = "/users")
public class UserController {
    private final UserService userService;

    @GetMapping
    public List<User> findAll() {
        log.info("GET /users");
        return userService.findAll();
    }

    @GetMapping("{userId}")
    public User findById(@PathVariable long userId) {
        log.info("GET /users/{}", userId);
        return userService.findById(userId);
    }

    @PostMapping
    public User create(@RequestBody User user) {
        log.info("POST /users/");
        return userService.create(user);
    }

    @PatchMapping("{userId}")
    public User update(@PathVariable long userId, @RequestBody User user) {
        log.info("PATCH /users/{}", userId);
        return userService.update(userId, user);
    }

    @DeleteMapping("{userId}")
    public void delete(@PathVariable long userId) {
        log.info("DELETE /users/{}", userId);
        userService.delete(userId);
    }

}
