package ru.practicum.shareit.item.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/items")
public class ItemController {
    private final ItemService service;
    private final String userHeader = "X-Sharer-User-Id";


    @GetMapping
    public List<Item> findAllByUserId(@RequestHeader(userHeader) Long userId) {
        log.info("GET userId={} /items", userId);
        return service.findAllByUserId(userId);
    }

    @GetMapping("{itemId}")
    public Item findByItemId(
            @PathVariable long itemId
    ) {
        log.info("GET /items/{}", itemId);
        return service.findByItemId(itemId);
    }

    @PostMapping
    public Item create(
            @RequestHeader(userHeader) Long userId,
            @RequestBody Item item
    ) {
        log.info("POST userId={} /items", userId);
        return service.create(userId, item);
    }

    @PatchMapping("{itemId}")
    public Item update(
            @RequestHeader(userHeader) Long userId,
            @PathVariable long itemId,
            @RequestBody Item item
    ) {
        log.info("PATCH userId={} /items/{}", userId, itemId);
        return service.update(userId, itemId, item);
    }

    @GetMapping("/search")
    public List<Item> findAllByName(
            @RequestHeader(userHeader) Long userId,
            @RequestParam String text
    ) {
        log.info("GET userId={} /items/search?text={}", userId, text);
        return service.findAllByName(userId, text);
    }
}
