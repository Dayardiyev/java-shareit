package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingCreateRequest;
import ru.practicum.shareit.booking.dto.BookingResponse;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import java.util.List;

import static ru.practicum.shareit.common.Constants.USER_HEADER;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingService bookingService;

    /**
     * Получение бронирования по идентификатору
     *
     * @param userId идентификатор пользователя может относиться к пользователю,
     *               который бронирует, или к владельцу вещи.
     * @param bookingId идентификатор бронирования
     * @return найденное бронирование
     */
    @GetMapping("{bookingId}")
    public BookingResponse findById(
            @RequestHeader(USER_HEADER) Long userId,
            @PathVariable Long bookingId
    ) {
        log.info("Получение бронирования по идентификатору {} пользователем {}", bookingId, userId);
        return bookingService.findByUserIdAndId(bookingId, userId);
    }

    /**
     * Получение списка по бронированию пользователя
     *
     * @param bookerId идентификатор пользователя
     * @param state состояние бронирования, возможные значения
     *              <ul>
     *                  <li>ALL</li>
     *                  <li>CURRENT</li>
     *                  <li>PAST</li>
     *                  <li>FUTURE</li>
     *                  <li>WAITING</li>
     *                  <li>APPROVED</li>
     *                  <li>REJECTED</li>
     *              </ul>
     * @return найденные бронирования
     */
    @GetMapping
    public List<BookingResponse> findAllByBookerId(
            @RequestHeader(USER_HEADER) Long bookerId,
            @RequestParam(defaultValue = "ALL") String state
    ) {
        log.info("Получение списка по бронированию пользователя {}", bookerId);
        return bookingService.findAllByBookerId(bookerId, state);
    }

    /**
     * Получение списка по бронированию владельца предмета
     *
     * @param ownerId идентификатор владельца предмета
     * @param state состояние бронирования, возможные значения
     *              <ul>
     *                  <li>ALL</li>
     *                  <li>CURRENT</li>
     *                  <li>PAST</li>
     *                  <li>FUTURE</li>
     *                  <li>WAITING</li>
     *                  <li>APPROVED</li>
     *                  <li>REJECTED</li>
     *              </ul>
     * @return найденные бронирования
     */
    @GetMapping("/owner")
    public List<BookingResponse> findAllByOwnerId(
            @RequestHeader(USER_HEADER) Long ownerId,
            @RequestParam(defaultValue = "ALL") String state
    ) {
        log.info("Получение списка по бронированию владельца предмета ownerId={}", ownerId);
        return bookingService.findAllByOwnerId(ownerId, state);
    }

    /**
     * Создание бронирования
     *
     * @param bookerId идентификатор пользователя
     * @param bookingCreateRequest параметры создания бронирования
     * @return созданный объект
     */
    @PostMapping
    public BookingResponse create(
            @RequestHeader(USER_HEADER) Long bookerId,
            @RequestBody @Valid BookingCreateRequest bookingCreateRequest
    ) {
        log.info("Создание бронирования пользователем с id={}", bookerId);
        return bookingService.create(bookerId, bookingCreateRequest);
    }

    /**
     * Подтверждение или отклонение бронирования
     *
     * @param ownerId идентификатор владельца
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
        return bookingService.approve(ownerId, bookingId, available);
    }
}
