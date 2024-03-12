package ru.practicum.shareit.server.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.server.booking.model.Booking;
import ru.practicum.shareit.server.booking.repository.BookingRepository;
import ru.practicum.shareit.server.booking.model.Status;
import ru.practicum.shareit.server.common.ObjectGenerator;
import ru.practicum.shareit.server.item.model.Item;
import ru.practicum.shareit.server.item.repository.ItemRepository;
import ru.practicum.shareit.server.user.model.User;
import ru.practicum.shareit.server.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@DataJpaTest
@Import(ObjectGenerator.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRepositoryIT {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final TestEntityManager testEntityManager;
    private final ObjectGenerator objectGenerator;

    private User owner1;
    private User owner2;

    private Item item11;
    private Item item12;
    private Item item21;
    private Item item22;

    @BeforeEach
    void setUp() {
        owner1 = objectGenerator.next(User.class);
        owner2 = objectGenerator.next(User.class);
        userRepository.save(owner1);
        userRepository.save(owner2);

        item11 = objectGenerator.next(Item.class);
        item12 = objectGenerator.next(Item.class);
        item21 = objectGenerator.next(Item.class);
        item22 = objectGenerator.next(Item.class);

        item11.setOwner(owner1);
        item12.setOwner(owner1);
        item21.setOwner(owner2);
        item22.setOwner(owner2);
    }

    @Test
    void getByOwnerIdOrderByIdAsc_whenItemsOfOwner2Exists_thenTwoItemOrderedByIdAscReturned() {
        itemRepository.save(item11);
        itemRepository.save(item12);
        itemRepository.save(item21);
        itemRepository.save(item22);
        PageRequest pageRequest = PageRequest.of(0, 50);

        List<Item> actualItems = itemRepository.findAllByOwnerIdOrderById(owner2.getId(), pageRequest).getContent();

        assertThat(actualItems, contains(item21, item22));
    }

    @Test
    void findAvailableBySubstring_whenTwoItemMatchCriteriaAndTwoAvailable_thenTwoItemReturned() {
        String searchString = "дрель";
        item11.setDescription("Аккумуляторная дрель");
        item12.setDescription("Просто дрель");
        item21.setDescription("Горный велосипед");
        item22.setDescription("Газонокосилка");
        item11.setAvailable(true);
        item12.setAvailable(true);
        item21.setAvailable(true);
        item22.setAvailable(true);
        itemRepository.save(item11);
        itemRepository.save(item12);
        itemRepository.save(item21);
        itemRepository.save(item22);
        PageRequest pageRequest = PageRequest.of(0, 50);

        List<Item> actualItems = itemRepository.findAllByNameContainingIgnoreCase(searchString, pageRequest).getContent();

        assertThat(actualItems, containsInAnyOrder(item11, item12));
    }

    @Test
    void findAvailableBySubstring_whenTwoItemMatchCriteriaAndOnlyOneAvailable_thenOneItemReturned() {
        String searchString = "дрель";
        item11.setDescription("Аккумуляторная дрель");
        item12.setDescription("Просто дрель");
        item21.setDescription("Горный велосипед");
        item22.setDescription("Газонокосилка");
        item11.setAvailable(true);
        item12.setAvailable(false);
        item21.setAvailable(true);
        item22.setAvailable(true);
        itemRepository.save(item11);
        itemRepository.save(item12);
        itemRepository.save(item21);
        itemRepository.save(item22);
        PageRequest pageRequest = PageRequest.of(0, 50);

        List<Item> actualItems = itemRepository.findAllByNameContainingIgnoreCase(searchString, pageRequest).getContent();

        assertThat(actualItems, containsInAnyOrder(item11));
    }

    @Test
    void findAvailableBySubstring_whenItemMatchCaseInsensitiveCriteriaAndAvailable_thenOneItemReturned() {
        String searchString = "дРеЛь";
        item11.setDescription("Аккумуляторная дрель");
        item11.setAvailable(true);
        itemRepository.save(item11);
        PageRequest pageRequest = PageRequest.of(0, 50);

        List<Item> actualItems = itemRepository.findAllByNameContainingIgnoreCase(searchString, pageRequest).getContent();

        assertThat(actualItems, containsInAnyOrder(item11));
    }

    @Test
    void findAvailableBySubstring_whenSearchStringIsNull_thenEmptyListReturned() {
        String searchString = null;
        item11.setDescription("Аккумуляторная дрель");
        item11.setAvailable(true);
        itemRepository.save(item11);
        PageRequest pageRequest = PageRequest.of(0, 50);

        List<Item> actualItems = itemRepository.findAllByNameContainingIgnoreCase(searchString, pageRequest).getContent();

        assertThat(actualItems, hasSize(0));
    }

    @Test
    void findAvailableBySubstring_whenTwoItemsMatchCriteriaInNameOrDescriptionAndAvailable_thenTwoItemReturned() {
        String searchString = "дрель";
        item11.setDescription("Аккумуляторная дрель");
        item12.setName("Просто дрель");
        item21.setDescription("Горный велосипед");
        item22.setDescription("Газонокосилка");
        item11.setAvailable(true);
        item12.setAvailable(true);
        item21.setAvailable(true);
        item22.setAvailable(true);
        itemRepository.save(item11);
        itemRepository.save(item12);
        itemRepository.save(item21);
        itemRepository.save(item22);
        PageRequest pageRequest = PageRequest.of(0, 50);

        List<Item> actualItems = itemRepository.findAllByNameContainingIgnoreCase(searchString, pageRequest).getContent();

        assertThat(actualItems, containsInAnyOrder(item11, item12));
    }

    @Test
    void findAvailableBySubstring_whenSearchStringIsEmpty_thenEmptyListReturned() {
        String searchString = "";
        item11.setDescription("Аккумуляторная дрель");
        item11.setAvailable(true);
        itemRepository.save(item11);
        PageRequest pageRequest = PageRequest.of(0, 50);

        List<Item> actualItems = itemRepository.findAllByNameContainingIgnoreCase(searchString, pageRequest).getContent();

        assertThat(actualItems, hasSize(0));
    }

    @Test
    void findById_whenBookingExists_thenItemWithNextBookingAndLastBookingReturned() {
        User booker = objectGenerator.next(User.class);
        userRepository.save(booker);

        itemRepository.save(item11);

        Booking booking11 = objectGenerator.next(Booking.class);
        Booking booking12 = objectGenerator.next(Booking.class);
        Booking booking13 = objectGenerator.next(Booking.class);

        booking11.setItem(item11);
        booking12.setItem(item11);
        booking13.setItem(item11);

        booking11.setBooker(booker);
        booking12.setBooker(booker);
        booking13.setBooker(booker);

        booking11.setStatus(Status.APPROVED);
        booking12.setStatus(Status.APPROVED);
        booking13.setStatus(Status.APPROVED);

        final LocalDateTime CURRENT_DATE = LocalDateTime.now();

        booking11.setStart(CURRENT_DATE.minusYears(1));
        booking11.setEnd(CURRENT_DATE.minusYears(1).plusDays(1));

        booking12.setStart(CURRENT_DATE.minusMonths(1));
        booking12.setEnd(CURRENT_DATE.minusMonths(1).plusDays(1));

        booking13.setStart(CURRENT_DATE.plusMonths(1));
        booking13.setEnd(CURRENT_DATE.plusMonths(1).plusDays(1));

        bookingRepository.save(booking11);
        bookingRepository.save(booking12);
        bookingRepository.save(booking13);

        testEntityManager.clear();

        Optional<Item> actualItem = itemRepository.findById(item11.getId());

        assertThat(actualItem.isPresent(), is(true));
        assertThat(actualItem.get().getLastBooking(), equalTo(booking12));
        assertThat(actualItem.get().getNextBooking(), equalTo(booking13));
    }
}