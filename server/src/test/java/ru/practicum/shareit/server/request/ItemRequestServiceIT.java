package ru.practicum.shareit.server.request;

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
import ru.practicum.shareit.server.common.ObjectGenerator;
import ru.practicum.shareit.server.request.model.ItemRequest;
import ru.practicum.shareit.server.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.server.user.model.User;
import ru.practicum.shareit.server.user.service.UserService;

import java.util.List;

import static org.hamcrest.Matchers.equalTo;

@SpringBootTest
@Import(ObjectGenerator.class)
@AutoConfigureTestDatabase
@Transactional
@Rollback
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRequestServiceIT {

    private final ObjectGenerator objectGenerator;
    private final ItemRequestServiceImpl itemRequestService;
    private final UserService userService;

    private User author1;
    private User author2;
    private ItemRequest itemRequest1;
    private ItemRequest itemRequest2;

    @BeforeEach
    void setUp() {
        author1 = objectGenerator.next(User.class);
        author2 = objectGenerator.next(User.class);

        itemRequest1 = objectGenerator.next(ItemRequest.class);
        itemRequest2 = objectGenerator.next(ItemRequest.class);

        author1 = userService.create(author1);
        author2 = userService.create(author2);

        itemRequest1.setAuthor(author1);
        itemRequest1.setAuthor(author2);

        itemRequest1 = itemRequestService.create(author1.getId(), itemRequest1);
        itemRequest2 = itemRequestService.create(author2.getId(), itemRequest2);
    }

    @Test
    void getAllMyRequests_whenOneRequestExists_thenListOfOneRequestReturned() {
        long userId = author1.getId();

        List<ItemRequest> foundItemRequests = itemRequestService.findAllByAuthor(userId);

        MatcherAssert.assertThat(foundItemRequests, Matchers.contains(itemRequest1));
    }

    @Test
    void getAllNotUserRequests_whenOtherRequestExists_thenListOfOneRequestReturned() {
        long userId = author2.getId();
        int from = 0;
        int size = 10;

        List<ItemRequest> foundItemRequests = itemRequestService.findAll(userId, from, size);

        MatcherAssert.assertThat(foundItemRequests, Matchers.contains(itemRequest1));
    }

    @Test
    void getByRequestId_whenRequestExists_thenItemRequestReturned() {
        long userId = author1.getId();
        long requestId = itemRequest2.getId();

        ItemRequest foundItemRequest = itemRequestService.findById(userId, requestId);

        MatcherAssert.assertThat(foundItemRequest, equalTo(itemRequest2));

    }
}