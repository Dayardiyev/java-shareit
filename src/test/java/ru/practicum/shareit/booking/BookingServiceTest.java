package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.common.exception.NotAvailableException;
import ru.practicum.shareit.common.exception.NotFoundException;
import ru.practicum.shareit.common.exception.BadRequestException;
import ru.practicum.shareit.common.ObjectGenerator;
import ru.practicum.shareit.common.exception.OwnerBookItemException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private final ObjectGenerator objectGenerator = new ObjectGenerator();

    private Item item;
    private User owner;
    private User booker;
    private Booking booking;

    @BeforeEach
    void setUp() {
        owner = objectGenerator.next(User.class);
        booker = objectGenerator.next(User.class);
        item = objectGenerator.next(Item.class);
        booking = objectGenerator.next(Booking.class);

        owner.setId(1L);
        booker.setId(2L);
        item.setId(1L);
        booking.setId(1L);

        item.setOwner(owner);
        booking.setItem(item);
        booking.setBooker(booker);
    }

    @Test
    void getById_whenInvalidBookingId_thenEntityNotFoundExceptionThrown() {
        long userId = 1L;
        long bookingId = 0L;

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(owner));
        when(bookingRepository.findByIdAndItemOwnerIdOrderByStartDesc(bookingId, userId))
                .thenReturn(Optional.empty());
        when(bookingRepository.findByIdAndBookerId(bookingId, userId))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.findByUserIdAndId(bookingId, userId));

        verify(userRepository).findById(userId);
        verify(bookingRepository).findByIdAndItemOwnerIdOrderByStartDesc(bookingId, userId);
        verify(bookingRepository).findByIdAndBookerId(bookingId, userId);
    }

    @Test
    void getById_whenUserOwnerAndNotBooker_thenBookingReturned() {
        long userId = owner.getId();
        long bookingId = 1L;

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(owner));
        when(bookingRepository.findByIdAndItemOwnerIdOrderByStartDesc(bookingId, userId))
                .thenReturn(Optional.of(booking));

        Booking actualBooking = bookingService.findByUserIdAndId(bookingId, userId);

        assertThat(actualBooking, is(booking));
        verify(userRepository).findById(userId);
        verify(bookingRepository).findByIdAndItemOwnerIdOrderByStartDesc(bookingId, userId);
    }

    @Test
    void getById_whenUserBookerAndNotOwner_thenBookingReturned() {
        long userId = booker.getId();
        long bookingId = 1L;

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(booker));
        when(bookingRepository.findByIdAndItemOwnerIdOrderByStartDesc(bookingId, userId))
                .thenReturn(Optional.empty());
        when(bookingRepository.findByIdAndBookerId(bookingId, userId))
                .thenReturn(Optional.of(booking));

        Booking actualBooking = bookingService.findByUserIdAndId(bookingId, userId);

        assertThat(actualBooking, is(booking));
        verify(userRepository).findById(userId);
        verify(bookingRepository).findByIdAndItemOwnerIdOrderByStartDesc(bookingId, userId);
        verify(bookingRepository).findByIdAndBookerId(bookingId, userId);
    }

    @Test
    void getById_whenUserNotOwnerAndNotBooker_thenForbiddenExceptionThrown() {
        long userId = 100L;
        long bookingId = 0L;

        when(userRepository.findById(userId))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.findByUserIdAndId(bookingId, userId));

        verify(userRepository).findById(userId);
    }

    @Test
    void findByBookerId_whenBookerIdIsInvalid_thanEntityNotFoundExceptionThrown() {
        long userId = 1L;
        int from = 0;
        int size = 50;
        BookingState stateFilter = BookingState.ALL;

        when(userRepository.findById(userId))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> bookingService.findAllByBookerId(userId, stateFilter, from, size));
    }

    @Test
    void findByBookerId_whenStateAll_thanListOfBookingsReturned() {
        BookingState stateFilter = BookingState.ALL;
        long userId = 1L;
        int from = 0;
        int size = 50;

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(booker));
        when(bookingRepository.findAllByBookerIdOrderByStartDesc(anyLong(), any()))
                .thenReturn(new PageImpl<>(List.of(booking)));

        List<Booking> actualBookings = bookingService.findAllByBookerId(userId, stateFilter, from, size);

        assertThat(actualBookings, contains(booking));
        verify(userRepository).findById(userId);
        verify(bookingRepository).findAllByBookerIdOrderByStartDesc(anyLong(), any());
    }

    @Test
    void findByBookerId_whenStateCurrent_thanListOfBookingsReturned() {
        BookingState stateFilter = BookingState.CURRENT;
        long userId = 1L;
        int from = 0;
        int size = 50;

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(booker));
        when(bookingRepository.findAllByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(
                anyLong(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(booking)));

        List<Booking> actualBookings = bookingService.findAllByBookerId(userId, stateFilter, from, size);

        assertThat(actualBookings, contains(booking));
        verify(userRepository).findById(userId);
        verify(bookingRepository).findAllByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(
                anyLong(), any(), any());
    }

    @Test
    void findByBookerId_whenStatePast_thanListOfBookingsReturned() {
        BookingState stateFilter = BookingState.PAST;
        long userId = 1L;
        int from = 0;
        int size = 50;

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(booker));
        when(bookingRepository.findAllByBookerIdAndEndIsBeforeOrderByStartDesc(
                anyLong(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(booking)));

        List<Booking> actualBookings = bookingService.findAllByBookerId(userId, stateFilter, from, size);

        assertThat(actualBookings, contains(booking));
        verify(userRepository).findById(userId);
        verify(bookingRepository).findAllByBookerIdAndEndIsBeforeOrderByStartDesc(
                anyLong(), any(), any());
    }

    @Test
    void findByBookerId_whenStateFuture_thanListOfBookingsReturned() {
        BookingState stateFilter = BookingState.FUTURE;
        long userId = 1L;
        int from = 0;
        int size = 50;

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(booker));
        when(bookingRepository.findAllByBookerIdAndStartIsAfterOrderByStartDesc(
                anyLong(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(booking)));

        List<Booking> actualBookings = bookingService.findAllByBookerId(userId, stateFilter, from, size);

        assertThat(actualBookings, contains(booking));
        verify(userRepository).findById(userId);
        verify(bookingRepository).findAllByBookerIdAndStartIsAfterOrderByStartDesc(
                anyLong(), any(), any());
    }

    @ParameterizedTest
    @EnumSource(value = BookingState.class, names = {"REJECTED", "APPROVED", "WAITING"})
    void findByBookerId_whenStateRejectedOrApprovedOrWaiting_thanListOfBookingsReturned(BookingState stateFilter) {
        long userId = 1L;
        int from = 0;
        int size = 50;

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(booker));
        when(bookingRepository.findAllByBookerIdAndStatusIs(
                anyLong(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(booking)));

        List<Booking> actualBookings = bookingService.findAllByBookerId(userId, stateFilter, from, size);

        assertThat(actualBookings, contains(booking));
        verify(userRepository).findById(userId);
        verify(bookingRepository).findAllByBookerIdAndStatusIs(
                anyLong(), any(), any());
    }

    @Test
    void findByItemOwner_whenOwnerIdIsInvalid_thanEntityNotFoundExceptionThrown() {
        long userId = 1L;
        int from = 0;
        int size = 50;
        BookingState stateFilter = BookingState.ALL;

        when(userRepository.findById(userId))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> bookingService.findAllByOwnerId(userId, stateFilter, from, size));
    }

    @Test
    void findByItemOwner_whenStateAll_thanListOfBookingsReturned() {
        BookingState stateFilter = BookingState.ALL;
        long userId = 1L;
        int from = 0;
        int size = 50;

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(booker));
        when(bookingRepository.findAllByItemOwnerIdOrderByStartDesc(anyLong(), any()))
                .thenReturn(new PageImpl<>(List.of(booking)));

        List<Booking> actualBookings = bookingService.findAllByOwnerId(userId, stateFilter, from, size);

        assertThat(actualBookings, contains(booking));
        verify(userRepository).findById(userId);
        verify(bookingRepository).findAllByItemOwnerIdOrderByStartDesc(anyLong(), any());
    }

    @Test
    void findByItemOwner_whenStateCurrent_thanListOfBookingsReturned() {
        BookingState stateFilter = BookingState.CURRENT;
        long userId = 1L;
        int from = 0;
        int size = 50;

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(booker));
        when(bookingRepository.findAllByOwnerIdCurrent(
                anyLong(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(booking)));

        List<Booking> actualBookings = bookingService.findAllByOwnerId(userId, stateFilter, from, size);

        assertThat(actualBookings, contains(booking));
        verify(userRepository).findById(userId);
        verify(bookingRepository).findAllByOwnerIdCurrent(
                anyLong(), any(), any());
    }

    @Test
    void findByItemOwner_whenStatePast_thanListOfBookingsReturned() {
        BookingState stateFilter = BookingState.PAST;
        long userId = 1L;
        int from = 0;
        int size = 50;

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(booker));
        when(bookingRepository.findAllByItemOwnerIdAndEndIsBeforeOrderByStartDesc(
                anyLong(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(booking)));

        List<Booking> actualBookings = bookingService.findAllByOwnerId(userId, stateFilter, from, size);

        assertThat(actualBookings, contains(booking));
        verify(userRepository).findById(userId);
        verify(bookingRepository).findAllByItemOwnerIdAndEndIsBeforeOrderByStartDesc(
                anyLong(), any(), any());
    }

    @Test
    void findByItemOwner_whenStateFuture_thanListOfBookingsReturned() {
        BookingState stateFilter = BookingState.FUTURE;
        long userId = 1L;
        int from = 0;
        int size = 50;

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(booker));
        when(bookingRepository.findAllByItemOwnerIdAndStartIsAfterOrderByStartDesc(
                anyLong(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(booking)));

        List<Booking> actualBookings = bookingService.findAllByOwnerId(userId, stateFilter, from, size);

        assertThat(actualBookings, contains(booking));
        verify(userRepository).findById(userId);
        verify(bookingRepository).findAllByItemOwnerIdAndStartIsAfterOrderByStartDesc(
                anyLong(), any(), any());
    }

    @ParameterizedTest
    @EnumSource(value = BookingState.class, names = {"REJECTED", "APPROVED", "WAITING"})
    void findByItemOwner_whenStateRejectedOrApprovedOrWaiting_thanListOfBookingsReturned(BookingState stateFilter) {
        long userId = 1L;
        int from = 0;
        int size = 50;

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(booker));
        when(bookingRepository.findAllByItemOwnerIdAndStatusIs(
                anyLong(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(booking)));

        List<Booking> actualBookings = bookingService.findAllByOwnerId(userId, stateFilter, from, size);

        assertThat(actualBookings, contains(booking));
        verify(userRepository).findById(userId);
        verify(bookingRepository).findAllByItemOwnerIdAndStatusIs(
                anyLong(), any(), any());
    }

    @Test
    void create_whenUserIdIsInvalid_thanEntityNotFoundExceptionThrown() {
        long userId = 0L;

        when(userRepository.findById(userId))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> bookingService.create(userId, booking));

        verify(userRepository).findById(userId);
        verify(bookingRepository, never()).save(booking);
    }

    @Test
    void create_whenItemIdIsInvalid_thanEntityNotFoundExceptionThrown() {
        long userId = 0L;

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(booker));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> bookingService.create(userId, booking));

        verify(userRepository).findById(userId);
        verify(itemRepository).findById(anyLong());
        verify(bookingRepository, never()).save(booking);
    }

    @Test
    void create_whenItemNotAvailable_thanBookingExceptionThrown() {
        long userId = 0L;
        item.setAvailable(false);

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(booker));
        when(itemRepository.findById(item.getId()))
                .thenReturn(Optional.of(item));

        assertThrows(NotAvailableException.class,
                () -> bookingService.create(userId, booking));

        verify(userRepository).findById(userId);
        verify(itemRepository).findById(item.getId());
        verify(bookingRepository, never()).save(booking);
    }

    @Test
    void create_whenOwnerTriesToBook_thanForbiddenExceptionThrown() {
        long userId = owner.getId();

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(owner));
        when(itemRepository.findById(item.getId()))
                .thenReturn(Optional.of(item));

        assertThrows(OwnerBookItemException.class,
                () -> bookingService.create(userId, booking));

        verify(userRepository).findById(userId);
        verify(itemRepository).findById(item.getId());
        verify(bookingRepository, never()).save(booking);
    }


    @Test
    void create_whenAllValid_thanEntityNotFoundExceptionThrown() {
        long userId = booker.getId();
        booking.setStart(LocalDateTime.of(2022, 1, 1, 0, 0));
        booking.setEnd(LocalDateTime.of(2023, 1, 1, 0, 0));

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(booker));
        when(itemRepository.findById(item.getId()))
                .thenReturn(Optional.of(item));

        bookingService.create(userId, booking);

        verify(userRepository).findById(userId);
        verify(itemRepository).findById(item.getId());
        verify(bookingRepository).save(booking);
    }

    @Test
    void setApproved_whenInvalidBookingId_thenEntityNotFoundExceptionThrown() {
        long bookingId = 0L;
        long userId = 1L;
        boolean approved = true;
        Status currentStatus = booking.getStatus();

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(owner));
        when(bookingRepository.findById(bookingId))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> bookingService.approve(userId, bookingId, approved));

        verify(bookingRepository).findById(bookingId);
        verify(bookingRepository, never()).save(booking);
        assertThat(booking.getStatus(), equalTo(currentStatus));
    }

    @Test
    void setApproved_whenInvalidUserId_thenEntityNotFoundExceptionThrown() {
        long bookingId = 1L;
        long userId = 1L;
        boolean approved = true;
        Status currentStatus = booking.getStatus();

        when(userRepository.findById(userId))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> bookingService.approve(userId, bookingId, approved));

        verify(userRepository).findById(userId);
        verify(bookingRepository, never()).save(booking);
        verify(bookingRepository, never()).findById(bookingId);
        assertThat(booking.getStatus(), equalTo(currentStatus));
    }

    @Test
    void setApproved_whenUserNotOwner_thenForbiddenExceptionThrown() {
        long bookingId = 1L;
        long userId = booker.getId();
        boolean approved = true;
        Status currentStatus = booking.getStatus();

        when(bookingRepository.findById(bookingId))
                .thenReturn(Optional.of(booking));
        when(userRepository.findById(userId))
                .thenReturn(Optional.of(booker));

        assertThrows(NotFoundException.class,
                () -> bookingService.approve(userId, bookingId, approved));

        verify(bookingRepository).findById(bookingId);
        verify(userRepository).findById(userId);
        verify(bookingRepository, never()).save(booking);
        assertThat(booking.getStatus(), equalTo(currentStatus));
    }

    @Test
    void setApproved_whenStatusNotWaiting_thenBookingExceptionThrown() {
        long bookingId = 1L;
        long userId = owner.getId();
        boolean approved = true;
        booking.setStatus(Status.REJECTED);
        Status currentStatus = booking.getStatus();

        when(bookingRepository.findById(bookingId))
                .thenReturn(Optional.of(booking));
        when(userRepository.findById(userId))
                .thenReturn(Optional.of(booker));

        assertThrows(BadRequestException.class,
                () -> bookingService.approve(userId, bookingId, approved));

        verify(bookingRepository).findById(bookingId);
        verify(userRepository).findById(userId);
        verify(bookingRepository, never()).save(booking);
        assertThat(booking.getStatus(), equalTo(currentStatus));
    }

    @Test
    void setApproved_whenAllValid_thenStatusChanged() {
        long bookingId = 1L;
        long userId = owner.getId();
        boolean approved = true;
        booking.setStatus(Status.WAITING);

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(owner));
        when(bookingRepository.findById(bookingId))
                .thenReturn(Optional.of(booking));
        when(bookingRepository.save(booking))
                .thenReturn(booking);

        Booking changedBooking = bookingService.approve(userId, bookingId, approved);

        verify(bookingRepository).findById(bookingId);
        verify(userRepository).findById(userId);
        verify(bookingRepository).save(booking);
        assertThat(changedBooking.getStatus(), equalTo(Status.APPROVED));
    }
}