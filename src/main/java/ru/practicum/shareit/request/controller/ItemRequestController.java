package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestCreateRequest;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.dto.ItemRequestResponse;
import ru.practicum.shareit.request.service.ItemRequestService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import java.util.List;

import static ru.practicum.shareit.common.Constants.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@Validated
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private final ItemRequestService service;
    private final ItemRequestMapper mapper;

    /**
     * Получение списка запросов по автору
     *
     * @param authorId идентификатор автора запроса
     * @return найденные запросы
     */
    @GetMapping
    public List<ItemRequestResponse> findAllByAuthor(
            @RequestHeader(USER_HEADER) Long authorId
    ) {
        log.info("Получение списка запросов по автору {}", authorId);
        return mapper.mapToResponseEntity(service.findAllByAuthor(authorId));
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
    public List<ItemRequestResponse> findAll(
            @RequestHeader(USER_HEADER) Long userId,
            @PositiveOrZero @RequestParam(defaultValue = DEFAULT_FROM) int from,
            @Positive @RequestParam(defaultValue = DEFAULT_SIZE) int size
    ) {
        log.info("Получение всех запросов для пользователя {} from={}, size={}", userId, from, size);
        return mapper.mapToResponseEntity(service.findAll(userId, from, size));
    }

    /**
     * Получение запроса по идентификатору
     *
     * @param userId идентификатор пользователя
     * @param requestId идентификатор запроса
     * @return найденный запрос
     */
    @GetMapping("{requestId}")
    public ItemRequestResponse findById(
            @RequestHeader(USER_HEADER) Long userId,
            @PathVariable long requestId
    ) {
        log.info("Получение запроса по идентификатору {} для пользователя {}", requestId, userId);
        return mapper.mapToResponseEntity(service.findById(userId, requestId));
    }

    /**
     * Создание запроса
     *
     * @param userId идентификатор пользователя
     * @param createRequest параметры запроса для создания объекта запроса
     * @return созданный запрос
     */
    @PostMapping
    public ItemRequestResponse create(
            @RequestHeader(USER_HEADER) Long userId,
            @RequestBody @Valid ItemRequestCreateRequest createRequest
    ) {
        log.info("Создание запроса от пользователя {}", userId);
        return mapper.mapToResponseEntity(
                service.create(userId, mapper.mapFromCreateRequestDto(createRequest))
        );
    }
}
