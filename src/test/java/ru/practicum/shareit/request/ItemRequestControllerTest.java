package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.common.ObjectGenerator;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.controller.ItemRequestController;
import ru.practicum.shareit.request.dto.ItemRequestCreateRequest;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.dto.ItemRequestResponse;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Set;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemRequestControllerTest {

    private final ObjectGenerator objectGenerator = new ObjectGenerator();

    @Mock
    private ItemRequestService itemRequestService;

    @InjectMocks
    private ItemRequestController controller;

    @Spy
    private ItemRequestMapper mapper = Mappers.getMapper(ItemRequestMapper.class);

    private ItemRequestCreateRequest itemRequestCreateRequest;
    private ItemRequest itemRequest;

    @BeforeEach
    void setUp() {
        itemRequestCreateRequest = objectGenerator.next(ItemRequestCreateRequest.class);
        itemRequest = objectGenerator.next(ItemRequest.class);
        Item item = objectGenerator.next(Item.class);
        User author = objectGenerator.next(User.class);

        User owner = objectGenerator.next(User.class);

        owner.setId(1L);
        author.setId(2L);

        item.setId(1L);

        itemRequest.setId(1L);
        itemRequest.setAuthor(author);
        itemRequest.setItems(Set.of(item));
    }

    @Test
    void create_whenValidItermRequestCreateRequest_thenItemRequestResponseReturned() {
        long userId = 1L;

        when(itemRequestService.create(anyLong(), any()))
                .thenReturn(itemRequest);

        ItemRequestResponse response = controller.create(userId, itemRequestCreateRequest);

        assertThat(response.getDescription(), equalTo(itemRequest.getDescription()));
        verify(itemRequestService).create(anyLong(), any());
    }

    @Test
    void getAllMyRequests_whenValidRequest_thenListOfItemRequestReturned() {
        long userId = 1L;

        when(itemRequestService.findAllByAuthor(userId))
                .thenReturn(List.of(itemRequest));

        List<ItemRequestResponse> response = controller.findAllByAuthor(userId);

        assertThat(response, hasSize(1));
        assertThat(response.get(0).getDescription(), equalTo(itemRequest.getDescription()));
        verify(itemRequestService).findAllByAuthor(userId);
    }

    @Test
    void getAllNotMyRequests_whenValidRequest_thenListOfItemRequestReturned() {
        long userId = 1L;
        int from = 0;
        int size = 0;

        when(itemRequestService.findAll(anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(itemRequest));

        List<ItemRequestResponse> response = controller.findAll(userId, from, size);

        assertThat(response, hasSize(1));
        assertThat(response.get(0).getDescription(), equalTo(itemRequest.getDescription()));
        verify(itemRequestService).findAll(anyLong(), anyInt(), anyInt());
    }

    @Test
    void getByRequestId_whenValidRequest_thenItemRequestReturned() {
        long userId = 1L;
        long itemRequestId = 1L;

        when(itemRequestService.findById(userId, itemRequestId))
                .thenReturn(itemRequest);

        ItemRequestResponse response = controller.findById(userId, itemRequestId);

        assertThat(response.getDescription(), equalTo(itemRequest.getDescription()));
        verify(itemRequestService).findById(userId, itemRequestId);
    }
}