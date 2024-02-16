package ru.practicum.shareit.item.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.common.dto.AbstractMapper;
import ru.practicum.shareit.item.model.Comment;

@Mapper(componentModel = "spring")
public interface CommentMapper extends AbstractMapper<Comment, CommentResponse, CommentCreateRequest, Void> {
    @Override
    @Mapping(target = "authorName",
            expression = "java(entity.getAuthor().getName())")
    @Mapping(target = "created", source = "entity.createdAt")
    CommentResponse mapToResponseEntity(Comment entity);
}
