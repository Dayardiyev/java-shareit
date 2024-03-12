package ru.practicum.shareit.server.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.server.booking.model.Booking;
import ru.practicum.shareit.server.common.ObjectGenerator;
import ru.practicum.shareit.server.item.controller.ItemController;
import ru.practicum.shareit.server.item.dto.*;
import ru.practicum.shareit.server.item.model.Comment;
import ru.practicum.shareit.server.item.model.Item;
import ru.practicum.shareit.server.item.service.ItemService;
import ru.practicum.shareit.server.user.model.User;

import java.util.List;
import java.util.Set;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemControllerTest {

    private final ObjectGenerator objectGenerator = new ObjectGenerator();

    @Mock
    private ItemService itemService;

    @InjectMocks
    private ItemController controller;

    @Spy
    private ItemMapper mapper = Mappers.getMapper(ItemMapper.class);

    @Spy
    private CommentMapper commentMapper = Mappers.getMapper(CommentMapper.class);

    private ItemCreateRequest itemCreateRequest;
    private ItemUpdateRequest itemUpdateRequest;
    private CommentCreateRequest commentCreateRequest;

    private User owner;
    private User booker;
    private Item item;
    private Comment comment;
    private Booking booking;

    @BeforeEach
    void setUp() {
        itemCreateRequest = objectGenerator.next(ItemCreateRequest.class);
        itemUpdateRequest = objectGenerator.next(ItemUpdateRequest.class);
        commentCreateRequest = objectGenerator.next(CommentCreateRequest.class);

        owner = objectGenerator.next(User.class);
        booker = objectGenerator.next(User.class);
        item = objectGenerator.next(Item.class);
        comment = objectGenerator.next(Comment.class);
        booking = objectGenerator.next(Booking.class);

        owner.setId(1L);
        booker.setId(2L);
        item.setId(1L);
        item.setOwner(owner);
        comment.setId(1L);
        comment.setItem(item);
        comment.setAuthor(booker);
        booking.setId(1L);
        booking.setBooker(booker);
        item.setLastBooking(booking);
        item.setComments(Set.of(comment));
    }

    @Test
    void getByUserId_whenValidRequest_thenListOfItemReturned() {
        long userId = owner.getId();
        int from = 0;
        int size = 50;
        when(itemService.findAllByUserId(userId, from, size))
                .thenReturn(List.of(item));

        List<ItemResponse> response = controller.findAllByUserId(userId, from, size);

        assertThat(response, hasSize(1));
        assertThat(response.get(0).getName(), is(item.getName()));
        verify(itemService).findAllByUserId(userId, from, size);
    }

    @Test
    void getById_whenValidRequest_thenItemReturned() {
        long userId = owner.getId();
        long itemId = item.getId();
        when(itemService.findById(itemId))
                .thenReturn(item);

        ItemResponse response = controller.findById(userId, itemId);

        assertThat(response.getName(), equalTo(item.getName()));
        verify(itemService).findById(itemId);
    }

    @Test
    void create_whenValidRequest_thenCreateItemReturned() {
        long userId = 1L;
        when(itemService.create(anyLong(), any()))
                .thenReturn(item);

        ItemResponse response = controller.create(userId, itemCreateRequest);

        assertThat(response.getName(), equalTo(item.getName()));
        verify(itemService).create(anyLong(), any());
    }

    @Test
    void update_whenValidRequest_thenUpdatedItemReturned() {
        long userId = 1L;
        long itemId = 1L;
        when(itemService.update(anyLong(), anyLong(), any()))
                .thenReturn(item);

        ItemResponse response = controller.update(userId, itemId, itemUpdateRequest);

        assertThat(response.getAvailable(), equalTo(itemUpdateRequest.getAvailable()));
        verify(itemService).update(anyLong(), anyLong(), any());
    }


    @Test
    void search_whenValidRequest_thenListOfItemReturned() {
        int from = 0;
        int size = 50;
        String substring = "text";
        when(itemService.findAllByName(substring, from, size))
                .thenReturn(List.of(item));

        List<ItemResponse> response = controller.findAllByName(substring, from, size);

        assertThat(response, hasSize(1));
        assertThat(response.get(0).getRequestId(), equalTo(item.getRequestId()));
        assertThat(response.get(0).getName(), equalTo(item.getName()));
        verify(itemService).findAllByName(substring, from, size);
    }

    @Test
    void addComment_whenValidRequest_thenListOfItemReturned() {
        long userId = 1L;
        long itemId = 1L;

        when(itemService.addComment(anyLong(), anyLong(), any()))
                .thenReturn(comment);

        CommentResponse response = controller.createComment(userId, itemId, commentCreateRequest);

        assertThat(response.getText(), equalTo(commentCreateRequest.getText()));
        verify(itemService).addComment(anyLong(), anyLong(), any());
    }
}