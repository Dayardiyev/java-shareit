package ru.practicum.shareit.server.item.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemUpdateRequest {

    String name;

    String description;

    Boolean available;
}
