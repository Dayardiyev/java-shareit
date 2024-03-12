package ru.practicum.shareit.server.item.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemCreateRequest {

    @NotBlank(message = "Название вещи не должно быть пустым")
    String name;

    @NotBlank(message = "Описание вещи не должно быть пустым")
    String description;

    @NotNull(message = "Признак доступности вещи не может быть пустым.")
    Boolean available;

    Long requestId;
}
