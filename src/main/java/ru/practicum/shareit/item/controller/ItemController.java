package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.service.CommentService;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

import static ru.practicum.shareit.common.Constants.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@Validated
@RequestMapping("/items")
public class ItemController {

    private final ItemService service;

    private final CommentService commentService;

    private final ItemMapper itemMapper;

    private final CommentMapper commentMapper;

    /**
     * Получение списка вещей по id пользователя
     *
     * @param userId идентификатор пользователя
     * @return список вещей пользователя
     */
    @GetMapping
    public List<ItemResponse> findAllByUserId(
            @RequestHeader(USER_HEADER) Long userId,
            @PositiveOrZero @RequestParam(defaultValue = DEFAULT_FROM) int from,
            @Positive @RequestParam(defaultValue = DEFAULT_SIZE) int size
    ) {
        log.info("Получение списка вещей пользователя с id {}", userId);
        return itemMapper.mapToResponseEntity(service.findAllByUserId(userId, from, size));
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
        return itemMapper.mapToResponseEntity(service.findById(itemId), userId);
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
        return itemMapper.mapToResponseEntity(
                service.create(userId, itemMapper.mapFromCreateRequestDto(itemCreateRequest))
        );
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
        return itemMapper.mapToResponseEntity(
                service.update(userId, itemId, itemMapper.mapFromUpdateRequestDto(itemUpdateRequest))
        );
    }

    /**
     * Поиск доступных вещей по наименованию или по описанию
     *
     * @param text название вещи
     * @return список найденных доступных вещей
     */
    @GetMapping("/search")
    public List<ItemResponse> findAllByName(
            @RequestParam String text,
            @PositiveOrZero @RequestParam(defaultValue = DEFAULT_FROM) int from,
            @Positive @RequestParam(defaultValue = DEFAULT_SIZE) int size
    ) {
        log.info("Получение списка вещей по названию {}", text);
        return itemMapper.mapToResponseEntity(service.findAllByName(text, from, size));
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
        return commentMapper.mapToResponseEntity(
                commentService.addComment(userId, itemId, commentMapper.mapFromCreateRequestDto(commentCreateRequest))
        );
    }
}
