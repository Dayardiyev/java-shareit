package ru.practicum.shareit.gateway.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.server.request.dto.ItemRequestCreateRequest;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import static ru.practicum.shareit.server.common.Constants.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@Validated
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private final ItemRequestClient client;
    /**
     * Получение списка запросов по автору
     *
     * @param authorId идентификатор автора запроса
     * @return найденные запросы
     */
    @GetMapping
    public ResponseEntity<Object> findAllByAuthor(
            @RequestHeader(USER_HEADER) Long authorId
    ) {
        log.info("Получение списка запросов по автору {}", authorId);
        return client.findAllByAuthor(authorId);
    }

    /**
     * Получение всех запросов для пользователя
     *
     * @param userId идентификатор пользователя
     * @param from стартовый индекс пагинаций
     * @param size размер страницы
     * @return найденные запросы
     */
    @GetMapping("/all")
    public ResponseEntity<Object> findAll(
            @RequestHeader(USER_HEADER) Long userId,
            @PositiveOrZero @RequestParam(defaultValue = DEFAULT_FROM) int from,
            @Positive @RequestParam(defaultValue = DEFAULT_SIZE) int size
    ) {
        log.info("Получение всех запросов для пользователя {} from={}, size={}", userId, from, size);
        return client.findAll(userId, from, size);
    }

    /**
     * Получение запроса по идентификатору
     *
     * @param userId идентификатор пользователя
     * @param requestId идентификатор запроса
     * @return найденный запрос
     */
    @GetMapping("{requestId}")
    public ResponseEntity<Object> findById(
            @RequestHeader(USER_HEADER) Long userId,
            @PathVariable long requestId
    ) {
        log.info("Получение запроса по идентификатору {} для пользователя {}", requestId, userId);
        return client.findById(userId, requestId);
    }

    /**
     * Создание запроса
     *
     * @param userId идентификатор пользователя
     * @param createRequest параметры запроса для создания объекта запроса
     * @return созданный запрос
     */
    @PostMapping
    public ResponseEntity<Object> create(
            @RequestHeader(USER_HEADER) Long userId,
            @RequestBody @Valid ItemRequestCreateRequest createRequest
    ) {
        log.info("Создание запроса от пользователя {}", userId);
        return client.create(userId, createRequest);
    }
}
