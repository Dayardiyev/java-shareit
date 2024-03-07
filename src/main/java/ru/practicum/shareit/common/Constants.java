package ru.practicum.shareit.common;

import lombok.experimental.UtilityClass;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@UtilityClass
public class Constants {
    public static final String USER_HEADER = "X-Sharer-User-Id";
    public static final String DEFAULT_FROM = "0";
    public static final String DEFAULT_SIZE = "50";
    public static final int DEFAULT_FROM_NUMBER = 0;
    public static final int DEFAULT_SIZE_NUMBER = 50;
}
