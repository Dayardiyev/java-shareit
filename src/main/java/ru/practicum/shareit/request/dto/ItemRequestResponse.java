package ru.practicum.shareit.request.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.item.dto.ItemResponse;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemRequestResponse {
    Long id;

    String description;

    LocalDateTime created;

    Set<ItemResponse> items;
}
