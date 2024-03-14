package ru.practicum.shareit.server.booking;

import lombok.RequiredArgsConstructor;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.server.booking.model.Booking;
import ru.practicum.shareit.server.booking.model.BookingState;
import ru.practicum.shareit.server.booking.model.Status;
import ru.practicum.shareit.server.booking.service.BookingServiceImpl;
import ru.practicum.shareit.server.common.ObjectGenerator;
import ru.practicum.shareit.server.common.exception.NotFoundException;
import ru.practicum.shareit.server.item.model.Item;
import ru.practicum.shareit.server.item.service.ItemService;
import ru.practicum.shareit.server.user.model.User;
import ru.practicum.shareit.server.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@AutoConfigureTestDatabase
@SpringBootTest
@Import(ObjectGenerator.class)
@Transactional
@Rollback
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingServiceIT {

    private final UserService userService;
    private final ItemService itemService;
    private final BookingServiceImpl bookingService;
    private final ObjectGenerator objectGenerator;

    private Item item;
    private User owner;
    private User booker;
    private User anotherUser;
    private Booking booking;

    @BeforeEach
    void setUp() {
        LocalDateTime currentDateTime = LocalDateTime.now();

        owner = objectGenerator.next(User.class);
        booker = objectGenerator.next(User.class);
        anotherUser = objectGenerator.next(User.class);
        item = objectGenerator.next(Item.class);
        booking = objectGenerator.next(Booking.class);

        item.setOwner(owner);
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStart(currentDateTime.plusDays(1));
        booking.setEnd(currentDateTime.plusDays(3));
        booking.setStatus(Status.WAITING);

        owner = userService.create(owner);
        booker = userService.create(booker);
        anotherUser = userService.create(anotherUser);
        item = itemService.create(owner.getId(), item);
        booking = bookingService.create(booker.getId(), booking);
    }

    @Test
    void getById_whenBookingExistsAndRequestBooker_thenBookingReturned() {
        long bookingId = booking.getId();
        long userId = booker.getId();

        Booking foundBooking = bookingService.findByUserIdAndId(bookingId, userId);

        MatcherAssert.assertThat(foundBooking, Matchers.equalTo(booking));
    }

    @Test
    void getById_whenBookingExistsAndRequestNeitherOwnerNorBooker_thenBookingReturned() {
        long bookingId = booking.getId();
        long userId = anotherUser.getId();

        assertThrows(NotFoundException.class, () -> bookingService.findByUserIdAndId(userId, bookingId));
    }

    @Test
    void findByBookerId_whenBookingExists_thenListOfBookingsReturned() {
        long bookerId = booker.getId();
        BookingState stateFilter = BookingState.ALL;
        int from = 0;
        int size = 50;

        List<Booking> foundBookings = bookingService.findAllByBookerId(bookerId, stateFilter, from, size);

        MatcherAssert.assertThat(foundBookings, Matchers.contains(booking));
    }

    @Test
    void findByItemOwner_whenBookingExists_thenListOfBookingsReturned() {
        long itemOwnerId = item.getOwner().getId();
        BookingState stateFilter = BookingState.ALL;
        int from = 0;
        int size = 50;

        List<Booking> foundBookings = bookingService.findAllByOwnerId(itemOwnerId, stateFilter, from, size);

        MatcherAssert.assertThat(foundBookings, Matchers.contains(booking));
    }

    @Test
    void setApproved_whenBookingExists_thenApprovedBookingReturned() {
        long ownerId = item.getOwner().getId();
        long bookingId = booking.getId();
        boolean approved = true;

        bookingService.approve(ownerId, bookingId, approved);
        Booking foundBooking = bookingService.findByUserIdAndId(bookingId, ownerId);

        MatcherAssert.assertThat(foundBooking, Matchers.is(booking));
        MatcherAssert.assertThat(foundBooking.getStatus(), Matchers.is(Status.APPROVED));
    }
}
