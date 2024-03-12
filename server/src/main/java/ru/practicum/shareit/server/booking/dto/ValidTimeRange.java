package ru.practicum.shareit.server.booking.dto;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidTimeRangeValidator.class)
public @interface ValidTimeRange {

    String message() default "Указана неправильная дата бронирования!";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

