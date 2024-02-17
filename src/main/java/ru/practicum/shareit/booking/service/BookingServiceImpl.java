package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingCreateRequest;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingResponse;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.common.exception.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final BookingMapper mapper;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public List<BookingResponse> findAllByBookerId(long userId, String stateParam) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + userId + " не найден!"));

        LocalDateTime time = LocalDateTime.now();
        BookingState state = BookingState.parse(stateParam);

        switch (state) {
            case ALL:
                return mapper.mapToResponseEntity(bookingRepository.findAllByBookerIdOrderByStartDesc(userId));
            case FUTURE:
                return mapper.mapToResponseEntity(bookingRepository.findAllByBookerIdAndStartIsAfterOrderByStartDesc(userId, time));
            case PAST:
                return mapper.mapToResponseEntity(bookingRepository.findAllByBookerIdAndEndIsBeforeOrderByStartDesc(userId, time));
            case CURRENT:
                return mapper.mapToResponseEntity(bookingRepository.findAllByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(userId, time));
            default:
                Status status = Status.valueOf(stateParam);
                return mapper.mapToResponseEntity(bookingRepository.findAllByBookerIdAndStatusIs(userId, status));
        }
    }

    @Override
    public List<BookingResponse> findAllByOwnerId(long ownerId, String stateParam) {
        userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + ownerId + " не найден!"));

        LocalDateTime time = LocalDateTime.now();
        BookingState state = BookingState.parse(stateParam);

        switch (state) {
            case ALL:
                return mapper.mapToResponseEntity(bookingRepository.findAllByItemOwnerIdOrderByStartDesc(ownerId));
            case FUTURE:
                return mapper.mapToResponseEntity(bookingRepository.findAllByItemOwnerIdAndStartIsAfterOrderByStartDesc(ownerId, time));
            case PAST:
                return mapper.mapToResponseEntity(bookingRepository.findAllByItemOwnerIdAndEndIsBeforeOrderByStartDesc(ownerId, time));
            case CURRENT:
                return mapper.mapToResponseEntity(bookingRepository.findAllByOwnerIdCurrent(ownerId, time));
            default:
                Status status = Status.valueOf(stateParam);
                return mapper.mapToResponseEntity(bookingRepository.findAllByItemOwnerIdAndStatusIs(ownerId, status));
        }
    }

    @Override
    public BookingResponse findByUserIdAndId(long bookingId, long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + userId + " не найден!"));

        Booking booking = bookingRepository.findByIdAndItemOwnerIdOrderByStartDesc(bookingId, userId)
                .orElseGet(() -> bookingRepository.findByIdAndBookerId(bookingId, userId)
                        .orElseThrow(() -> new NotFoundException("Для пользователя с id=" + bookingId + " не найден бронь с id=" + bookingId)));

        return mapper.mapToResponseEntity(booking);
    }

    @Override
    public BookingResponse create(long bookerId, BookingCreateRequest bookingCreateRequest) {

        User booker = userRepository.findById(bookerId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + bookerId + " не найден!"));

        long itemId = bookingCreateRequest.getItemId();
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с id=" + itemId + " не найден!"));

        if (item.getOwner().equals(booker)) {
            throw new OwnerBookItemException("Владелец вещи не может бронировать вещи");
        }

        if (!item.getAvailable()) {
            throw new NotAvailableException("Вещь не доступен для бронирования");
        }

        Booking booking = mapper.mapFromCreateRequestDto(bookingCreateRequest);

        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(Status.WAITING);
        bookingRepository.save(booking);
        return mapper.mapToResponseEntity(booking);
    }

    @Override
    public BookingResponse approve(long ownerId, long bookingId, boolean approved) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + ownerId + " не найден!"));

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование с id=" + bookingId + " не найден!"));

        if (booking.getStatus() != Status.WAITING) {
            throw new BadRequestException("Статус можно менять только если WAITING");
        }

        Item item = booking.getItem();
        if (!item.getOwner().equals(owner)) {
            throw new NotFoundException("Пользователь с id=" + ownerId + " не является владельцем вещи с id=" + item.getId() + "!");
        }

        if (approved) {
            booking.setStatus(Status.APPROVED);
        } else {
            booking.setStatus(Status.REJECTED);
        }

        return mapper.mapToResponseEntity(bookingRepository.save(booking));
    }
}
