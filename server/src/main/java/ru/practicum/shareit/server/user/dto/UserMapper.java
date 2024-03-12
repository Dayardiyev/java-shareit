package ru.practicum.shareit.server.user.dto;

import org.mapstruct.Mapper;
import ru.practicum.shareit.server.common.dto.AbstractMapper;
import ru.practicum.shareit.server.user.model.User;

@Mapper(componentModel = "spring")
public interface UserMapper extends AbstractMapper<User, UserResponse, UserCreateRequest, UserUpdateRequest> {
}
