package ru.practicum.shareit.server.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.server.booking.model.Booking;
import ru.practicum.shareit.server.booking.model.Status;
import ru.practicum.shareit.server.booking.repository.BookingRepository;
import ru.practicum.shareit.server.common.ObjectGenerator;
import ru.practicum.shareit.server.item.model.Item;
import ru.practicum.shareit.server.item.repository.ItemRepository;
import ru.practicum.shareit.server.user.model.User;
import ru.practicum.shareit.server.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;

@DataJpaTest
@Import(ObjectGenerator.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingRepositoryIT {

    private static final LocalDateTime FIRST_START_DATE = LocalDateTime.of(2021, 1, 1, 0, 0);
    private static final LocalDateTime FIRST_END_DATE = LocalDateTime.of(2021, 2, 1, 0, 0);
    private static final LocalDateTime SECOND_START_DATE = LocalDateTime.of(2022, 1, 1, 0, 0);
    private static final LocalDateTime SECOND_END_DATE = LocalDateTime.of(2022, 2, 1, 0, 0);
    private static final LocalDateTime THIRD_START_DATE = LocalDateTime.of(2023, 1, 1, 0, 0);
    private static final LocalDateTime THIRD_END_DATE = LocalDateTime.of(2023, 2, 1, 0, 0);

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    private final ObjectGenerator objectGenerator;

    private User booker;
    private User owner;

    private Item item;

    private Booking booking1;
    private Booking booking2;
    private Booking booking3;

    private final PageRequest pageRequest = PageRequest.of(0, 50);

    @BeforeEach
    void setUp() {
        owner = objectGenerator.next(User.class);
        booker = objectGenerator.next(User.class);
        userRepository.save(owner);
        userRepository.save(booker);

        item = objectGenerator.next(Item.class);
        item.setOwner(owner);
        item = itemRepository.save(item);

        booking1 = objectGenerator.next(Booking.class);
        booking1.setBooker(booker);
        booking1.setItem(item);
        booking1.setStart(FIRST_START_DATE);
        booking1.setEnd(FIRST_END_DATE);
        booking1.setStatus(Status.WAITING);
        bookingRepository.save(booking1);

        booking2 = objectGenerator.next(Booking.class);
        booking2.setBooker(booker);
        booking2.setItem(item);
        booking2.setStart(SECOND_START_DATE);
        booking2.setEnd(SECOND_END_DATE);
        booking2.setStatus(Status.APPROVED);
        bookingRepository.save(booking2);

        booking3 = objectGenerator.next(Booking.class);
        booking3.setBooker(booker);
        booking3.setItem(item);
        booking3.setStart(THIRD_START_DATE);
        booking3.setEnd(THIRD_END_DATE);
        booking3.setStatus(Status.REJECTED);
        bookingRepository.save(booking3);
    }

    @Test
    void findByBookerIdOrderByStartDesc_whenBookingsExists_thenOrderedListOfBookingsReturned() {
        List<Booking> actualBookings = bookingRepository.findAllByBookerIdOrderByStartDesc(booker.getId(), pageRequest)
                .getContent();

        assertThat(actualBookings, hasSize(3));
        assertThat(actualBookings.stream().map(Booking::getStart).collect(Collectors.toList()),
                contains(THIRD_START_DATE, SECOND_START_DATE, FIRST_START_DATE));
    }

    @Test
    void findByBookerIdAndStartLessThanEqualAndEndGreaterThanEqualOrderByStartDesc_whenExists_thenReturned() {
        LocalDateTime searchDateTime = FIRST_START_DATE.plusDays(1);

        List<Booking> bookings =
                bookingRepository.findAllByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(
                        booker.getId(), searchDateTime, pageRequest
                ).getContent();

        assertThat(bookings, hasSize(1));
    }

    @Test
    void findByBookerIdAndEndLessThanOrderByStartDesc() {
        LocalDateTime searchDateTime = SECOND_END_DATE.plusDays(1);

        List<Booking> bookings = bookingRepository.findAllByBookerIdAndEndIsBeforeOrderByStartDesc(booker.getId(),
                searchDateTime, pageRequest).getContent();

        assertThat(bookings.stream().map(Booking::getStart).collect(Collectors.toList()),
                contains(SECOND_START_DATE, FIRST_START_DATE));
    }

    @Test
    void findByBookerIdAndStartGreaterThanOrderByStartDesc() {
        LocalDateTime searchDateTime = FIRST_END_DATE.minusDays(1);

        List<Booking> bookings = bookingRepository.findAllByBookerIdAndStartIsAfterOrderByStartDesc(booker.getId(),
                searchDateTime, pageRequest).getContent();

        assertThat(bookings.stream().map(Booking::getStart).collect(Collectors.toList()),
                contains(THIRD_START_DATE, SECOND_START_DATE));
    }

    @Test
    void findByBookerIdAndStatusIsOrderByStartDesc() {
        List<Booking> bookings = bookingRepository.findAllByBookerIdAndStatusIs(booker.getId(),
                Status.REJECTED, pageRequest).getContent();

        assertThat(bookings, hasSize(1));
    }

    @Test
    void findByItemOwnerIdOrderByStartDesc() {
        List<Booking> bookings = bookingRepository.findAllByItemOwnerIdOrderByStartDesc(owner.getId(), pageRequest)
                .getContent();

        assertThat(bookings.stream().map(Booking::getStart).collect(Collectors.toList()),
                contains(THIRD_START_DATE, SECOND_START_DATE, FIRST_START_DATE));
    }

    @Test
    void findByItemOwnerIdAndStartLessThanEqualAndEndGreaterThanEqualOrderByStartDesc() {
        LocalDateTime searchDateTime = FIRST_START_DATE.plusDays(1);

        List<Booking> bookings =
                bookingRepository.findAllByOwnerIdCurrent(
                        owner.getId(), searchDateTime, pageRequest
                ).getContent();

        assertThat(bookings, hasSize(1));
    }

    @Test
    void findByItemOwnerIdAndEndLessThanOrderByStartDesc() {
        LocalDateTime searchDateTime = SECOND_END_DATE.plusDays(1);

        List<Booking> bookings = bookingRepository.findAllByItemOwnerIdAndEndIsBeforeOrderByStartDesc(
                owner.getId(),
                searchDateTime,
                pageRequest).getContent();

        assertThat(bookings.stream().map(Booking::getStart).collect(Collectors.toList()), contains(SECOND_START_DATE,
                FIRST_START_DATE));
    }

    @Test
    void findByItemOwnerIdAndStartGreaterThanOrderByStartDesc() {
        LocalDateTime searchDateTime = FIRST_END_DATE.minusDays(1);

        List<Booking> bookings = bookingRepository.findAllByItemOwnerIdAndStartIsAfterOrderByStartDesc(
                owner.getId(),
                searchDateTime,
                pageRequest).getContent();

        assertThat(bookings.stream().map(Booking::getStart).collect(Collectors.toList()),
                contains(THIRD_START_DATE, SECOND_START_DATE));
    }

    @Test
    void findByItemOwnerIdAndStatusIsOrderByStartDesc() {
        List<Booking> bookings = bookingRepository.findAllByItemOwnerIdAndStatusIs(owner.getId(),
                Status.REJECTED, pageRequest).getContent();

        assertThat(bookings, hasSize(1));
    }

    @Test
    void findByItemId() {
        List<Booking> bookings = bookingRepository.findAllByItemId(item.getId(), pageRequest).getContent();

        assertThat(bookings, contains(booking1, booking2, booking3));
    }
}