package ru.practicum.shareit.server.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler
    public ErrorResponse handleNotFound(NotFoundException e) {
        log.warn(e.getMessage());
        return new ErrorResponse(e.getMessage(), e.getCause());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({ConstraintViolationException.class, NotAvailableException.class, BadRequestException.class, MethodArgumentNotValidException.class})
    public ErrorResponse handleBadRequest(Exception e) {
        log.warn(e.getMessage());
        return new ErrorResponse(e.getMessage(), e.getCause());
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ErrorResponse handleDataIntegrity(DataIntegrityViolationException e) {
        log.warn(e.getMessage());
        return new ErrorResponse(e.getMessage(), e.getCause());
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler
    public ErrorResponse handleOwnerBookItem(OwnerBookItemException e) {
        log.warn(e.getMessage());
        return new ErrorResponse(e.getMessage(), e.getCause());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler
    public ErrorResponse handleIllegal(IllegalArgumentException e) {
        log.warn(e.getMessage());
        return new ErrorResponse(e.getMessage(), e.getCause());
    }
}


