package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.common.ObjectGenerator;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.*;

@SpringBootTest
@Import(ObjectGenerator.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@AutoConfigureTestDatabase
@Transactional
@Rollback
class ItemServiceIT {

    private final ObjectGenerator objectGenerator;
    private final ItemServiceImpl itemService;
    private final UserService userService;
    private final BookingService bookingService;

    private User owner;
    private User booker;
    private Item item;
    private Comment comment;
    private Booking booking;

    @BeforeEach
    void setUp() {
        owner = objectGenerator.next(User.class);
        booker = objectGenerator.next(User.class);
        item = objectGenerator.next(Item.class);
        comment = objectGenerator.next(Comment.class);
        booking = objectGenerator.next(Booking.class);

        item.setName("item name");
        item.setDescription("item description");

        booker = userService.create(booker);
        owner = userService.create(owner);
        item = itemService.create(owner.getId(), item);
    }

    @Test
    void getByUserId_whenItemsExist_thenListOfItemsReturned() {
        long userId = owner.getId();
        int from = 0;
        int size = 10;

        List<Item> foundItems = itemService.findAllByUserId(userId, from, size);

        MatcherAssert.assertThat(foundItems, contains(item));
    }

    @Test
    void getById_whenItemExists_thenItemReturned() {
        long itemId = item.getId();

        Item foundItem = itemService.findById(itemId);

        MatcherAssert.assertThat(foundItem, equalTo(item));
    }

    @Test
    void update_whenItemExists_thenUpdatedItemReturned() {
        long userId = owner.getId();
        long itemId = item.getId();

        Item updateItem = new Item();
        updateItem.setName("Updated name");
        updateItem.setDescription("Updated description");
        updateItem.setAvailable(false);

        Item updatedItem = itemService.update(userId, itemId, updateItem);

        MatcherAssert.assertThat(updatedItem.getName(), equalTo(updateItem.getName()));
        MatcherAssert.assertThat(updatedItem.getDescription(), equalTo(updateItem.getDescription()));
        MatcherAssert.assertThat(updatedItem.getAvailable(), equalTo(updateItem.getAvailable()));
    }

    @Test
    void findAvailableBySubstring_whenItemContainsInDescription_thenListOfItemsReturned() {
        String search = "description";
        int from = 0;
        int size = 10;

        List<Item> foundItems = itemService.findAllByName(search, from, size);

        MatcherAssert.assertThat(foundItems, contains(item));
    }

    @Test
    void findAvailableBySubstring_whenItemContainsInName_thenListOfItemsReturned() {
        String search = "name";
        int from = 0;
        int size = 10;

        List<Item> foundItems = itemService.findAllByName(search, from, size);

        MatcherAssert.assertThat(foundItems, contains(item));
    }

    @Test
    void addComment() {
        long userId = booker.getId();
        long itemId = item.getId();

        booking.setStart(LocalDateTime.of(2024, 1, 1, 0, 0));
        booking.setEnd(LocalDateTime.of(2024, 1, 10, 0, 0));
        booking.setItem(item);
        booking = bookingService.create(userId, booking);
        bookingService.approve(item.getOwner().getId(), booking.getId(), true);

        Comment createdComment = itemService.addComment(userId, itemId, comment);

        MatcherAssert.assertThat(createdComment.getId(), greaterThan(0L));
    }
}