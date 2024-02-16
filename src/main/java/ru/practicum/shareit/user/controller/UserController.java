package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserCreateRequest;
import ru.practicum.shareit.user.dto.UserResponse;
import ru.practicum.shareit.user.dto.UserUpdateRequest;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.List;

@RestController
@Slf4j
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * Получение всех пользователей
     * @return список пользователей
     */
    @GetMapping
    public List<UserResponse> findAll() {
        log.info("Получение всех пользователей");
        return userService.findAll();
    }

    /**
     * Получение пользователя по идентификатору.
     * @param id идентификатор пользователя.
     * @return найденный пользователь.
     */
    @GetMapping("{id}")
    public UserResponse findById(@PathVariable long id) {
        log.info("Получение пользователя по идентификатору");
        return userService.findById(id);
    }

    /**
     * Создание пользователя.
     * @param userCreateRequest параметры для создания пользователя.
     * @return созданный пользователь.
     */
    @PostMapping
    public UserResponse create(@RequestBody @Valid UserCreateRequest userCreateRequest) {
        log.info("Создание пользователя");
        return userService.create(userCreateRequest);
    }

    /**
     * Обновление пользователя.
     * @param id идентификатор пользователя для обновления.
     * @param userUpdateRequest параметры для обновления пользователя.
     * @return обновленный пользователь.
     */
    @PatchMapping("{id}")
    public UserResponse update(@PathVariable long id, @RequestBody UserUpdateRequest userUpdateRequest) {
        log.info("Обновление пользователя");
        return userService.update(id, userUpdateRequest);
    }

    /**
     * Удаление пользователя по идентификатору.
     * @param id идентификатор пользователя.
     */
    @DeleteMapping("{id}")
    public void delete(@PathVariable long id) {
        log.info("Удаление пользователя по идентификатору");
        userService.delete(id);
    }
}
