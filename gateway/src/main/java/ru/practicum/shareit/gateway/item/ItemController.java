package ru.practicum.shareit.gateway.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.server.item.dto.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import static ru.practicum.shareit.server.common.Constants.*;

@RequiredArgsConstructor
@RestController
@Slf4j
@Validated
@RequestMapping("/items")
public class ItemController {
    private final ItemClient client;

    /**
     * Получение списка вещей по id пользователя
     *
     * @param userId идентификатор пользователя
     * @return список вещей пользователя
     */
    @GetMapping
    public ResponseEntity<Object> findAllByUserId(
            @RequestHeader(USER_HEADER) Long userId,
            @PositiveOrZero @RequestParam(defaultValue = DEFAULT_FROM) int from,
            @Positive @RequestParam(defaultValue = DEFAULT_SIZE) int size
    ) {
        return client.findAllByUserId(userId, from, size);
    }

    /**
     * Получение вещи по идентификатору
     *
     * @param itemId идентификатор вещи
     * @return полученная вещь
     */
    @GetMapping("{itemId}")
    public ResponseEntity<Object> findById(
            @RequestHeader(USER_HEADER) long userId,
            @PathVariable long itemId
    ) {
        log.info("Получение вещи по идентификатору {}", itemId);
        return client.findById(userId, itemId);
    }

    /**
     * Создание вещи пользователя
     *
     * @param userId            идентификатор пользователя
     * @param itemCreateRequest запрос на создание вещи пользователя
     * @return созданная вещь
     */
    @PostMapping
    public ResponseEntity<Object> create(
            @RequestHeader(USER_HEADER) Long userId,
            @RequestBody @Valid ItemCreateRequest itemCreateRequest
    ) {
        System.out.println();
        log.info("Создание пользователем {} вещи \"{}\"", userId, itemCreateRequest.getName());
        return client.create(userId, itemCreateRequest);
    }

    /**
     * Редактирование вещи пользователя
     *
     * @param userId            идентификатор пользователя
     * @param itemId            идентификатор вещи
     * @param itemUpdateRequest параметры запросы для редактирования вещи
     * @return вещь
     */
    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> update(
            @RequestHeader(USER_HEADER) Long userId,
            @PathVariable long itemId,
            @RequestBody ItemUpdateRequest itemUpdateRequest
    ) {
        log.info("Редактирование пользователем {} вещи \"{}\"", userId, itemUpdateRequest.getName());
        return client.update(userId, itemId, itemUpdateRequest);
    }

    /**
     * Поиск доступных вещей по наименованию или по описанию
     *
     * @param text название вещи
     * @return список найденных доступных вещей
     */
    @GetMapping("/search")
    public ResponseEntity<Object> findAllByName(
            @RequestParam String text,
            @PositiveOrZero @RequestParam(defaultValue = DEFAULT_FROM) int from,
            @Positive @RequestParam(defaultValue = DEFAULT_SIZE) int size
    ) {
        log.info("Получение списка вещей по названию {}", text);
        return client.findAllByName(text, from, size);
    }


    /**
     * Создание комментарий для предмета
     *
     * @param userId               идентификатор пользователя брони
     * @param itemId               идентификатор предмета
     * @param commentCreateRequest параметры запроса комментарий
     * @return созданный комментарий
     */
    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(
            @RequestHeader(USER_HEADER) Long userId,
            @PathVariable long itemId,
            @RequestBody @Valid CommentCreateRequest commentCreateRequest
    ) {
        log.info("Создание комментарий для предмета {}", itemId);
        return client.addComment(userId, itemId, commentCreateRequest);
    }
}
