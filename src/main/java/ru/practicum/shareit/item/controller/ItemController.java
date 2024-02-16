package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.service.CommentService;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;

import static ru.practicum.shareit.common.Constants.USER_HEADER;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/items")
public class ItemController {

    private final ItemService service;

    private final CommentService commentService;

    /**
     * Получение списка вещей по id пользователя
     *
     * @param userId идентификатор пользователя
     * @return список вещей пользователя
     */
    @GetMapping
    public List<ItemResponse> findAllByUserId(@RequestHeader(USER_HEADER) Long userId) {
        log.info("Получение списка вещей пользователя с id {}", userId);
        return service.findAllByUserId(userId);
    }

    /**
     * Получение вещи по идентификатору
     *
     * @param itemId идентификатор вещи
     * @return полученная вещь
     */
    @GetMapping("{itemId}")
    public ItemResponse findById(
            @RequestHeader(USER_HEADER) long userId,
            @PathVariable long itemId
    ) {
        log.info("Получение вещи по идентификатору {}", itemId);
        return service.findById(itemId, userId);
    }

    /**
     * Создание вещи пользователя
     *
     * @param userId идентификатор пользователя
     * @param itemCreateRequest запрос на создание вещи пользователя
     * @return созданная вещь
     */
    @PostMapping
    public ItemResponse create(
            @RequestHeader(USER_HEADER) Long userId,
            @RequestBody @Valid ItemCreateRequest itemCreateRequest
    ) {
        log.info("Создание пользователем {} вещи \"{}\"", userId, itemCreateRequest.getName());
        return service.create(userId, itemCreateRequest);
    }

    /**
     * Редактирование вещи пользователя
     *
     * @param userId идентификатор пользователя
     * @param itemId идентификатор вещи
     * @param itemUpdateRequest параметры запросы для редактирования вещи
     * @return вещь
     */
    @PatchMapping("{itemId}")
    public ItemResponse update(
            @RequestHeader(USER_HEADER) Long userId,
            @PathVariable long itemId,
            @RequestBody ItemUpdateRequest itemUpdateRequest
    ) {
        log.info("Редактирование пользователем {} вещи \"{}\"", userId, itemUpdateRequest.getName());
        return service.update(userId, itemId, itemUpdateRequest);
    }

    /**
     * Поиск доступных вещей по наименованию или по описанию
     *
     * @param text название вещи
     * @return список найденных доступных вещей
     */
    @GetMapping("/search")
    public List<ItemResponse> findAllByName(
            @RequestParam String text
    ) {
        log.info("Получение списка вещей по названию {}", text);
        return service.findAllByName(text);
    }


    /**
     * Создание комментарий для предмета
     *
     * @param userId идентификатор пользователя брони
     * @param itemId идентификатор предмета
     * @param commentCreateRequest параметры запроса комментарий
     * @return созданный комментарий
     */
    @PostMapping("/{itemId}/comment")
    public CommentResponse createComment(
            @RequestHeader(USER_HEADER) Long userId,
            @PathVariable long itemId,
            @RequestBody @Valid CommentCreateRequest commentCreateRequest
    ) {
        log.info("Создание комментарий для предмета {}", itemId);
        return commentService.create(userId, itemId, commentCreateRequest);
    }
}
