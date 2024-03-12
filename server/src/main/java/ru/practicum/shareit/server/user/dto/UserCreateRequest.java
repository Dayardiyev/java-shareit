package ru.practicum.shareit.server.user.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserCreateRequest {

    @NotBlank(message = "Имя пользователя не может быть пустым.")
    String name;

    @NotBlank(message = "Электронная почта не может быть пустым.")
    @Email(message = "Электронная почта не соответствует формату \"user@mail.com\".")
    String email;
}
