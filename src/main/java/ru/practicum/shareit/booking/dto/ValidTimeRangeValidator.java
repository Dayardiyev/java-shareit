package ru.practicum.shareit.booking.dto;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;

public class ValidTimeRangeValidator implements ConstraintValidator<ValidTimeRange, BookingCreateRequest> {

    @Override
    public void initialize(ValidTimeRange constraintAnnotation) {
    }

    @Override
    public boolean isValid(BookingCreateRequest request, ConstraintValidatorContext context) {
        if (request == null) {
            return false;
        }
        LocalDateTime start = request.getStart();
        LocalDateTime end = request.getEnd();

        return start != null && end != null && !start.isAfter(end) && !end.isBefore(start) && !start.isEqual(end);
    }
}
