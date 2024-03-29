package ru.practicum.shareit.gateway.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.server.booking.dto.BookingCreateRequest;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import static ru.practicum.shareit.server.common.Constants.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@Validated
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingClient client;

    /**
     * Получение бронирования по идентификатору
     *
     * @param userId    идентификатор пользователя может относиться к пользователю,
     *                  который бронирует, или к владельцу вещи.
     * @param bookingId идентификатор бронирования
     * @return найденное бронирование
     */
    @GetMapping("{bookingId}")
    public ResponseEntity<Object> findById(
            @RequestHeader(USER_HEADER) Long userId,
            @PathVariable Long bookingId
    ) {
        log.info("Получение бронирования по идентификатору {} пользователем {}", bookingId, userId);
        return client.findByUserIdAndId(bookingId, userId);
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
    public ResponseEntity<Object> findAllByBookerId(
            @RequestHeader(USER_HEADER) Long bookerId,
            @RequestParam(value = "state", defaultValue = "ALL") String stateFilter,
            @PositiveOrZero @RequestParam(defaultValue = DEFAULT_FROM) int from,
            @Positive @RequestParam(defaultValue = DEFAULT_SIZE) int size

    ) {
        log.info("Получение списка по бронированию пользователя {}", bookerId);
        return client.findAllByBookerId(bookerId, stateFilter, from, size);
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
    public ResponseEntity<Object> findAllByOwnerId(
            @RequestHeader(USER_HEADER) Long ownerId,
            @RequestParam(value = "state", defaultValue = "ALL") String stateFilter,
            @PositiveOrZero @RequestParam(defaultValue = DEFAULT_FROM) int from,
            @Positive @RequestParam(defaultValue = DEFAULT_SIZE) int size
    ) {
        log.info("Получение списка по бронированию владельца предмета ownerId={}", ownerId);
        return client.findAllByOwnerId(ownerId, stateFilter, from, size);
    }

    /**
     * Создание бронирования
     *
     * @param bookerId             идентификатор пользователя
     * @param bookingCreateRequest параметры создания бронирования
     * @return созданный объект
     */
    @PostMapping
    public ResponseEntity<Object> create(
            @RequestHeader(USER_HEADER) Long bookerId,
            @RequestBody @Valid BookingCreateRequest bookingCreateRequest
    ) {
        log.info("Создание бронирования пользователем с id={}", bookerId);
        return client.create(bookerId, bookingCreateRequest);
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
    public ResponseEntity<Object> update(
            @RequestHeader(USER_HEADER) Long ownerId,
            @PathVariable Long bookingId,
            @RequestParam(name = "approved") Boolean available
    ) {
        log.info("Подтверждение или отклонение бронирования владельцем={} статус={}", ownerId, available);
        return client.approve(ownerId, bookingId, available);
    }
}
