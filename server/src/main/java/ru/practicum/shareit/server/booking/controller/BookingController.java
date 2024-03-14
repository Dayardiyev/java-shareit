package ru.practicum.shareit.server.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.server.booking.dto.BookingCreateRequest;
import ru.practicum.shareit.server.booking.dto.BookingMapper;
import ru.practicum.shareit.server.booking.dto.BookingResponse;
import ru.practicum.shareit.server.booking.model.BookingState;
import ru.practicum.shareit.server.booking.service.BookingService;

import java.util.List;

import static ru.practicum.shareit.server.common.Constants.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingService bookingService;
    private final BookingMapper mapper;

    /**
     * Получение бронирования по идентификатору
     *
     * @param userId    идентификатор пользователя может относиться к пользователю,
     *                  который бронирует, или к владельцу вещи.
     * @param bookingId идентификатор бронирования
     * @return найденное бронирование
     */
    @GetMapping("{bookingId}")
    public BookingResponse findById(
            @RequestHeader(USER_HEADER) Long userId,
            @PathVariable Long bookingId
    ) {
        log.info("Получение бронирования по идентификатору {} пользователем {}", bookingId, userId);
        return mapper.mapToResponseEntity(bookingService.findByUserIdAndId(bookingId, userId));
    }

    /**
     * Получение списка по бронированию пользователя
     *
     * @param bookerId    идентификатор пользователя
     * @param stateFilter состояние бронирования, возможные значения
     *                    <ul>
     *                        <li>ALL</li>
     *                        <li>CURRENT</li>
     *                        <li>PAST</li>
     *                        <li>FUTURE</li>
     *                        <li>WAITING</li>
     *                        <li>APPROVED</li>
     *                        <li>REJECTED</li>
     *                    </ul>
     * @return найденные бронирования
     */
    @GetMapping
    public List<BookingResponse> findAllByBookerId(
            @RequestHeader(USER_HEADER) Long bookerId,
            @RequestParam(value = "state", defaultValue = "ALL") String stateFilter,
            @RequestParam(defaultValue = DEFAULT_FROM) int from,
            @RequestParam(defaultValue = DEFAULT_SIZE) int size

    ) {
        log.info("Получение списка по бронированию пользователя {}", bookerId);
        BookingState state = BookingState.parse(stateFilter);
        return mapper.mapToResponseEntity(bookingService.findAllByBookerId(bookerId, state, from, size));
    }

    /**
     * Получение списка по бронированию владельца предмета
     *
     * @param ownerId     идентификатор владельца предмета
     * @param stateFilter состояние бронирования, возможные значения
     *                    <ul>
     *                        <li>ALL</li>
     *                        <li>CURRENT</li>
     *                        <li>PAST</li>
     *                        <li>FUTURE</li>
     *                        <li>WAITING</li>
     *                        <li>APPROVED</li>
     *                        <li>REJECTED</li>
     *                    </ul>
     * @return найденные бронирования
     */
    @GetMapping("/owner")
    public List<BookingResponse> findAllByOwnerId(
            @RequestHeader(USER_HEADER) Long ownerId,
            @RequestParam(value = "state", defaultValue = "ALL") String stateFilter,
            @RequestParam(defaultValue = DEFAULT_FROM) int from,
            @RequestParam(defaultValue = DEFAULT_SIZE) int size
    ) {
        log.info("Получение списка по бронированию владельца предмета ownerId={}", ownerId);
        BookingState state = BookingState.parse(stateFilter);
        return mapper.mapToResponseEntity(bookingService.findAllByOwnerId(ownerId, state, from, size));
    }

    /**
     * Создание бронирования
     *
     * @param bookerId             идентификатор пользователя
     * @param bookingCreateRequest параметры создания бронирования
     * @return созданный объект
     */
    @PostMapping
    public BookingResponse create(
            @RequestHeader(USER_HEADER) Long bookerId,
            @RequestBody BookingCreateRequest bookingCreateRequest
    ) {
        log.info("Создание бронирования пользователем с id={}", bookerId);
        return mapper.mapToResponseEntity(
                bookingService.create(bookerId, mapper.mapFromCreateRequestDto(bookingCreateRequest))
        );
    }

    /**
     * Подтверждение или отклонение бронирования
     *
     * @param ownerId   идентификатор владельца
     * @param bookingId идентификатор бронирования
     * @param available подтверждение или отклонение
     * @return изменённое бронирование
     */
    @PatchMapping("/{bookingId}")
    public BookingResponse update(
            @RequestHeader(USER_HEADER) Long ownerId,
            @PathVariable Long bookingId,
            @RequestParam(name = "approved") Boolean available
    ) {
        log.info("Подтверждение или отклонение бронирования владельцем={} статус={}", ownerId, available);
        return mapper.mapToResponseEntity(bookingService.approve(ownerId, bookingId, available));
    }
}
