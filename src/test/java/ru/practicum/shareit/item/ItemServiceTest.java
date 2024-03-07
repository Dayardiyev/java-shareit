package ru.practicum.shareit.item;

import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.common.ObjectGenerator;
import ru.practicum.shareit.common.exception.BadRequestException;
import ru.practicum.shareit.common.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {

    private final ObjectGenerator objectGenerator = new ObjectGenerator();

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private ItemServiceImpl itemService;

    @Spy
    private ItemMapper mapper = Mappers.getMapper(ItemMapper.class);

    private Item item;
    private User user;
    private Comment comment;
    private Booking booking;

    @BeforeEach
    void setUp() {
        user = objectGenerator.next(User.class);
        item = objectGenerator.next(Item.class);
        comment = objectGenerator.next(Comment.class);
        booking = objectGenerator.next(Booking.class);

        user.setId(1L);
        item.setId(1L);
        comment.setId(1L);
        booking.setId((1L));
    }

    @Test
    void getByUserId_whenValidParameters_thenListReturned() {
        long userId = 1L;
        int from = 0;
        int size = 10;

        when(itemRepository.findAllByOwnerIdOrderById(anyLong(), any()))
                .thenReturn(new PageImpl<>(List.of(item)));

        itemService.findAllByUserId(userId, from, size);

        verify(itemRepository).findAllByOwnerIdOrderById(anyLong(), any());
    }

    @Test
    void getById_whenValidParameters_thenItemReturned() {
        long itemId = 1L;

        when(itemRepository.findById(itemId))
                .thenReturn(Optional.of(item));

        itemService.findById(itemId);

        verify(itemRepository).findById(itemId);
    }

    @Test
    void getById_whenInvalidItemId_thenEntityNotFoundExceptionThrown() {
        long itemId = 0L;

        when(itemRepository.findById(itemId))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.findById(itemId));

        verify(itemRepository).findById(itemId);
    }

    @Test
    void create_whenUserExists_thenCreatedItemReturned() {
        long userId = 1L;

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));
        when(itemRepository.save(item))
                .thenReturn(item);

        Item createdItem = itemService.create(userId, item);

        MatcherAssert.assertThat(createdItem.getOwner(), equalTo(user));
        verify(userRepository).findById(userId);
        verify(itemRepository).save(item);
    }

    @Test
    void create_whenUserNotExists_thenEntityNotFoundExceptionThrown() {
        long userId = 1L;

        when(userRepository.findById(userId))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.create(userId, item));

        verify(userRepository).findById(userId);
        verify(itemRepository, never()).save(item);
    }

    @Test
    void update_whenItemExistsAndUserIsOwner_thenUpdatedItemReturned() {
        long userId = 1L;
        long itemId = 1L;
        item.setOwner(user);

        Item updateItem = new Item();
        updateItem.setName("Updated name");
        updateItem.setDescription("Updated description");
        updateItem.setAvailable(false);

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));
        when(itemRepository.findByIdAndOwnerId(itemId, userId))
                .thenReturn(Optional.of(item));
        when(itemRepository.save(item))
                .thenReturn(item);

        item = itemService.create(userId, item);
        item = itemService.update(userId, itemId, updateItem);

        MatcherAssert.assertThat(item.getName(), equalTo(updateItem.getName()));
        MatcherAssert.assertThat(item.getDescription(), equalTo(updateItem.getDescription()));
        MatcherAssert.assertThat(item.getAvailable(), equalTo(updateItem.getAvailable()));
        verify(userRepository).findById(userId);
        verify(itemRepository).findByIdAndOwnerId(itemId, userId);
        verify(itemRepository, times(2)).save(item);
    }

    @Test
    void update_whenItemExistsAndUserIsNotOwner_thenExceptionThrown() {
        long userId = 2L;
        long itemId = 1L;
        item.setOwner(user);

        Item updateItem = new Item();
        updateItem.setName("Updated name");
        updateItem.setDescription("Updated description");
        updateItem.setAvailable(false);

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));
        when(itemRepository.findByIdAndOwnerId(itemId, userId))
                .thenReturn(Optional.empty());
        when(itemRepository.save(item))
                .thenReturn(item);

        item = itemService.create(userId, item);

        assertThrows(NotFoundException.class, () -> itemService.update(userId, itemId, updateItem));
        verify(userRepository).findById(userId);
        verify(itemRepository).findByIdAndOwnerId(itemId, userId);
        verify(itemRepository, times(1)).save(item);
    }

    @Test
    void update_whenItemNotExists_thenNotFoundExceptionThrown() {
        long userId = 2L;
        long itemId = 1L;
        item.setOwner(user);

        Item updateItem = new Item();
        updateItem.setName("Updated name");
        updateItem.setDescription("Updated description");
        updateItem.setAvailable(false);

        when(itemRepository.findByIdAndOwnerId(itemId, userId))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.update(userId, itemId, updateItem));
        verify(itemRepository).findByIdAndOwnerId(itemId, userId);
        verify(itemRepository, never()).save(item);
    }

    @Test
    void findAvailableBySubstring_whenValidParameters_thenListReturned() {
        String text = "search";
        int from = 0;
        int size = 10;

        when(itemRepository.findAllByNameContainingIgnoreCase(anyString(), any()))
                .thenReturn(new PageImpl<>(List.of(item)));

        itemService.findAllByName(text, from, size);

        verify(itemRepository).findAllByNameContainingIgnoreCase(anyString(), any());
    }

    @Test
    void addComment_whenValidParameters_thenCreatedCommentReturned() {
        long userId = user.getId();
        long itemId = item.getId();
        booking.setItem(item);

        when(bookingRepository.findByBookerIdAndItemIdAndStatusIsAndStartIsBefore(
                anyLong(),
                anyLong(),
                any(Status.class),
                any(LocalDateTime.class),
                any(Pageable.class))
        ).thenReturn(new PageImpl<>(List.of(booking)));

        itemService.addComment(userId, itemId, comment);

        verify(commentRepository).save(comment);
    }

    @Test
    void addComment_whenInvalidUserId_thenExceptionThrown() {
        long userId = user.getId();
        long itemId = item.getId();
        booking.setItem(item);

        when(bookingRepository.findByBookerIdAndItemIdAndStatusIsAndStartIsBefore(
                anyLong(),
                anyLong(),
                any(Status.class),
                any(LocalDateTime.class),
                any(Pageable.class))
        ).thenReturn(new PageImpl<>(List.of()));


        assertThrows(BadRequestException.class, () -> itemService.addComment(userId, itemId, comment));
        verify(commentRepository, never()).save(comment);
    }

    @Test
    void addComment_whenItemId_thenExceptionThrown() {
        long userId = user.getId();
        long itemId = item.getId();
        booking.setItem(item);

        when(bookingRepository.findByBookerIdAndItemIdAndStatusIsAndStartIsBefore(
                anyLong(),
                anyLong(),
                any(Status.class),
                any(LocalDateTime.class),
                any(Pageable.class))
        ).thenReturn(new PageImpl<>(List.of()));

        assertThrows(BadRequestException.class, () -> itemService.addComment(userId, itemId, comment));
        verify(commentRepository, never()).save(comment);
    }
}