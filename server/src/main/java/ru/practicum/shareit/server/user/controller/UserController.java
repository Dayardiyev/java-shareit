package ru.practicum.shareit.server.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.server.user.dto.UserCreateRequest;
import ru.practicum.shareit.server.user.dto.UserMapper;
import ru.practicum.shareit.server.user.dto.UserResponse;
import ru.practicum.shareit.server.user.dto.UserUpdateRequest;
import ru.practicum.shareit.server.user.service.UserService;

import java.util.List;

import static ru.practicum.shareit.server.common.Constants.DEFAULT_FROM;
import static ru.practicum.shareit.server.common.Constants.DEFAULT_SIZE;

@RestController
@Slf4j
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserMapper mapper;

    /**
     * Получение всех пользователей
     *
     * @param from стартовый индекс для пагинаций
     * @param size размер страницы
     * @return список пользователей
     */
    @GetMapping
    public List<UserResponse> findAll(
            @RequestParam(defaultValue = DEFAULT_FROM) int from,
            @RequestParam(defaultValue = DEFAULT_SIZE) int size
    ) {
        log.info("Получение всех пользователей");
        return mapper.mapToResponseEntity(userService.findAll(from, size));
    }

    /**
     * Получение пользователя по идентификатору.
     *
     * @param id идентификатор пользователя.
     * @return найденный пользователь.
     */
    @GetMapping("{id}")
    public UserResponse findById(@PathVariable long id) {
        log.info("Получение пользователя по идентификатору");
        return mapper.mapToResponseEntity(userService.findById(id));
    }

    /**
     * Создание пользователя.
     *
     * @param userCreateRequest параметры для создания пользователя.
     * @return созданный пользователь.
     */
    @PostMapping
    public UserResponse create(@RequestBody UserCreateRequest userCreateRequest) {
        log.info("Создание пользователя");
        return mapper.mapToResponseEntity(
                userService.create(mapper.mapFromCreateRequestDto(userCreateRequest))
        );
    }

    /**
     * Обновление пользователя.
     *
     * @param id                идентификатор пользователя для обновления.
     * @param userUpdateRequest параметры для обновления пользователя.
     * @return обновленный пользователь.
     */
    @PatchMapping("{id}")
    public UserResponse update(@PathVariable long id, @RequestBody UserUpdateRequest userUpdateRequest) {
        log.info("Обновление пользователя");
        return mapper.mapToResponseEntity(
                userService.update(id, mapper.mapFromUpdateRequestDto(userUpdateRequest))
        );
    }

    /**
     * Удаление пользователя по идентификатору.
     *
     * @param id идентификатор пользователя.
     */
    @DeleteMapping("{id}")
    public void delete(@PathVariable long id) {
        log.info("Удаление пользователя по идентификатору");
        userService.delete(id);
    }
}
