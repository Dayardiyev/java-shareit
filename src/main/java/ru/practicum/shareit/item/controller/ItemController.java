package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.constants.Constants;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/items")
public class ItemController {
    private final ItemService service;

    @GetMapping
    public List<ItemDto> findAllByUserId(@RequestHeader(Constants.USER_HEADER) Long userId) {
        log.info("GET userId={} /items", userId);
        return service.findAllByUserId(userId);
    }

    @GetMapping("{itemId}")
    public ItemDto findByItemId(
            @PathVariable long itemId
    ) {
        log.info("GET /items/{}", itemId);
        return service.findById(itemId);
    }

    @PostMapping
    public ItemDto create(
            @RequestHeader(Constants.USER_HEADER) Long userId,
            @RequestBody @Valid ItemDto itemDto
    ) {
        log.info("POST userId={} /items", userId);
        return service.create(userId, itemDto);
    }

    @PatchMapping("{itemId}")
    public ItemDto update(
            @RequestHeader(Constants.USER_HEADER) Long userId,
            @PathVariable long itemId,
            @RequestBody ItemDto itemDto
    ) {
        log.info("PATCH userId={} /items/{}", userId, itemId);
        return service.update(userId, itemId, itemDto);
    }

    @GetMapping("/search")
    public List<ItemDto> findAllByName(
            @RequestHeader(Constants.USER_HEADER) Long userId,
            @RequestParam String text
    ) {
        log.info("GET userId={} /items/search?text={}", userId, text);
        return service.findAllByName(userId, text);
    }
}
