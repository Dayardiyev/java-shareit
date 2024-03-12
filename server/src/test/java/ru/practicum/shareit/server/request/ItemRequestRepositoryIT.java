package ru.practicum.shareit.server.request;

import lombok.RequiredArgsConstructor;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.server.common.ObjectGenerator;
import ru.practicum.shareit.server.request.model.ItemRequest;
import ru.practicum.shareit.server.request.repository.ItemRequestRepository;
import ru.practicum.shareit.server.user.model.User;
import ru.practicum.shareit.server.user.repository.UserRepository;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static ru.practicum.shareit.server.common.Constants.DEFAULT_FROM_NUMBER;
import static ru.practicum.shareit.server.common.Constants.DEFAULT_SIZE_NUMBER;


@DataJpaTest
@Import(ObjectGenerator.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRequestRepositoryIT {

    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ObjectGenerator objectGenerator;

    private User author1;
    private User author2;

    private ItemRequest itemRequest11;
    private ItemRequest itemRequest12;
    private ItemRequest itemRequest21;
    private ItemRequest itemRequest22;

    @BeforeEach
    void setUp() {
        author1 = objectGenerator.next(User.class);
        author2 = objectGenerator.next(User.class);
        userRepository.save(author1);
        userRepository.save(author2);

        itemRequest11 = objectGenerator.next(ItemRequest.class);
        itemRequest12 = objectGenerator.next(ItemRequest.class);

        itemRequest21 = objectGenerator.next(ItemRequest.class);
        itemRequest22 = objectGenerator.next(ItemRequest.class);
    }

    @Test
    void save_whenDescriptionIsNull_thenDataIntegrityViolationExceptionThrown() {
        userRepository.save(author1);
        itemRequest11.setAuthor(author1);
        itemRequest11.setDescription(null);

        assertThrows(DataIntegrityViolationException.class, () -> itemRequestRepository.save(itemRequest11));
    }

    @Test
    void save_whenAuthorIsNull_thenDataIntegrityViolationExceptionThrown() {
        itemRequest11.setAuthor(null);

        assertThrows(DataIntegrityViolationException.class, () -> itemRequestRepository.save(itemRequest11));
    }

    @Test
    void findAllByAuthorIdOrderByCreatedDesc_whenAuthor2RequestNotExists_thanEmptyListReturned() {
        itemRequest11.setAuthor(author1);
        itemRequest12.setAuthor(author1);
        itemRequestRepository.save(itemRequest11);
        itemRequestRepository.save(itemRequest12);

        List<ItemRequest> actualItems = itemRequestRepository.findAllByAuthor(author2);

        assertThat(actualItems, Matchers.hasSize(0));
    }

    @Test
    void findAllByAuthorIdNot_whenAuthor1HasRequest_thenOnlyRequestsOfAuthor2Returned() {
        itemRequest11.setAuthor(author1);
        itemRequest12.setAuthor(author1);
        itemRequest21.setAuthor(author2);
        itemRequest22.setAuthor(author2);
        itemRequestRepository.save(itemRequest11);
        itemRequestRepository.save(itemRequest12);
        itemRequestRepository.save(itemRequest21);
        itemRequestRepository.save(itemRequest22);
        PageRequest pageRequest = PageRequest.of(DEFAULT_FROM_NUMBER, DEFAULT_SIZE_NUMBER);

        List<ItemRequest> actualItems = itemRequestRepository.findAllByAuthorNotOrderByCreated(author1, pageRequest)
                .getContent();

        assertThat(actualItems, Matchers.hasSize(2));
        assertThat(actualItems, Matchers.containsInAnyOrder(itemRequest22, itemRequest21));
    }
}