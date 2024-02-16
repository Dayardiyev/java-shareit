package ru.practicum.shareit.user.dto;

import org.mapstruct.Mapper;
import ru.practicum.shareit.common.dto.AbstractMapper;
import ru.practicum.shareit.user.model.User;

@Mapper(componentModel = "spring")
public interface UserMapper extends AbstractMapper<User, UserResponse, UserCreateRequest, UserUpdateRequest> {
}
