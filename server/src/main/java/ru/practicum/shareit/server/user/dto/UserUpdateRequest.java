package ru.practicum.shareit.server.user.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.Email;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserUpdateRequest {

    String name;

    @Email(message = "Электронная почта не соответствует формату \"user@mail.com\".")
    String email;
}
