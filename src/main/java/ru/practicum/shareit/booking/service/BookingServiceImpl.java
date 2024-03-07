package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
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
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public List<Booking> findAllByBookerId(long userId, BookingState state, int from, int size) {
        getUserById(userId);

        LocalDateTime time = LocalDateTime.now();

        PageRequest pageRequest = PageRequest.of(from / size, size);
        Page<Booking> page;
        switch (state) {
            case ALL:
                page = bookingRepository.findAllByBookerIdOrderByStartDesc(userId, pageRequest);
                break;
            case FUTURE:
                page = bookingRepository.findAllByBookerIdAndStartIsAfterOrderByStartDesc(userId, time, pageRequest);
                break;
            case PAST:
                page = bookingRepository.findAllByBookerIdAndEndIsBeforeOrderByStartDesc(userId, time, pageRequest);
                break;
            case CURRENT:
                page = bookingRepository.findAllByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(userId, time, pageRequest);
                break;
            default:
                Status status = Status.valueOf(state.name());
                page = bookingRepository.findAllByBookerIdAndStatusIs(userId, status, pageRequest);
                break;
        }
        return page.getContent();
    }

    @Override
    public List<Booking> findAllByOwnerId(long ownerId, BookingState state, int from, int size) {
        getUserById(ownerId);

        LocalDateTime time = LocalDateTime.now();

        PageRequest pageRequest = PageRequest.of(from / size, size);
        Page<Booking> page;
        switch (state) {
            case ALL:
                page = bookingRepository.findAllByItemOwnerIdOrderByStartDesc(ownerId, pageRequest);
                break;
            case FUTURE:
                page = bookingRepository.findAllByItemOwnerIdAndStartIsAfterOrderByStartDesc(ownerId, time, pageRequest);
                break;
            case PAST:
                page = bookingRepository.findAllByItemOwnerIdAndEndIsBeforeOrderByStartDesc(ownerId, time, pageRequest);
                break;
            case CURRENT:
                page = bookingRepository.findAllByOwnerIdCurrent(ownerId, time, pageRequest);
                break;
            default:
                Status status = Status.valueOf(state.name());
                page = bookingRepository.findAllByItemOwnerIdAndStatusIs(ownerId, status, pageRequest);
                break;
        }
        return page.getContent();
    }

    @Override
    public Booking findByUserIdAndId(long bookingId, long userId) {
        getUserById(userId);

        return bookingRepository.findByIdAndItemOwnerIdOrderByStartDesc(bookingId, userId)
                .orElseGet(() -> bookingRepository.findByIdAndBookerId(bookingId, userId)
                        .orElseThrow(() -> new NotFoundException("Для пользователя с id=" + bookingId + " не найден бронь с id=" + bookingId)));
    }

    @Override
    public Booking create(long bookerId, Booking booking) {

        User booker = getUserById(bookerId);

        long itemId = booking.getItem().getId();
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с id=" + itemId + " не найден!"));

        if (item.getOwner().equals(booker)) {
            throw new OwnerBookItemException("Владелец вещи не может бронировать вещи");
        }

        if (!item.getAvailable()) {
            throw new NotAvailableException("Вещь не доступен для бронирования");
        }

        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(Status.WAITING);
        return bookingRepository.save(booking);
    }

    @Override
    public Booking approve(long ownerId, long bookingId, boolean approved) {
        User owner = getUserById(ownerId);

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование с id=" + bookingId + " не найден!"));

        if (booking.getStatus() != Status.WAITING) {
            throw new BadRequestException("Статус можно менять только если WAITING");
        }

        Item item = booking.getItem();
        if (!item.getOwner().equals(owner)) {
            throw new NotFoundException("Пользователь с id=" + ownerId + " не является владельцем вещи с id=" + item.getId() + "!");
        }

        booking.setStatus(approved ? Status.APPROVED : Status.REJECTED);

        return bookingRepository.save(booking);
    }

    private User getUserById(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + userId + " не найден!"));
    }
}
