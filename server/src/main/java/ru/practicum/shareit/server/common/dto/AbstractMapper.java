package ru.practicum.shareit.server.common.dto;

import org.mapstruct.BeanMapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.practicum.shareit.server.common.AbstractEntity;

import java.util.List;

/**
 * @param <T> Entity
 * @param <S> DTO response
 * @param <U> DTO create request
 * @param <V> DTO update request
 */
public interface AbstractMapper<T extends AbstractEntity, S, U, V> {

    S mapToResponseEntity(T entity);

    List<S> mapToResponseEntity(List<T> entities);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_DEFAULT)
    T mapFromCreateRequestDto(U entityCreateRequestDto);

    T mapFromUpdateRequestDto(V entityUpdateRequestDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void merge(@MappingTarget T entity, T sourceEntity);
}
