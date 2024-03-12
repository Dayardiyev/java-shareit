package ru.practicum.shareit.server.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import ru.practicum.shareit.server.common.exception.NotFoundException;
import ru.practicum.shareit.server.common.ObjectGenerator;
import ru.practicum.shareit.server.request.model.ItemRequest;
import ru.practicum.shareit.server.request.repository.ItemRequestRepository;
import ru.practicum.shareit.server.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.server.user.model.User;
import ru.practicum.shareit.server.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceTest {

    private final ObjectGenerator objectGenerator = new ObjectGenerator();

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;

    private User user;
    private ItemRequest itemRequest;

    @BeforeEach
    void setUp() {
        user = objectGenerator.next(User.class);
        itemRequest = objectGenerator.next(ItemRequest.class);
    }

    @Test
    void create_whenUserExists_thenCreatedItemRequestReturned() {
        long userId = 1L;

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        itemRequestService.create(userId, itemRequest);

        verify(userRepository).findById(userId);
        verify(itemRequestRepository).save(itemRequest);
    }

    @Test
    void create_whenUserNotExists_thenNotFoundExceptionThrown() {
        long userId = 1L;

        when(userRepository.findById(userId))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemRequestService.create(userId, itemRequest));

        verify(userRepository).findById(userId);
        verify(itemRequestRepository, never()).save(itemRequest);
    }


    @Test
    void findAllByAuthor_whenUserExists_thenListOfItemRequestReturned() {
        long userId = 1L;

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));
        when(itemRequestRepository.findAllByAuthor(any()))
                .thenReturn(List.of(itemRequest));

        itemRequestService.findAllByAuthor(userId);

        verify(userRepository).findById(userId);
        verify(itemRequestRepository).findAllByAuthor(any());
    }

    @Test
    void findAllByAuthor_whenUserNotExists_thenNotFoundExceptionThrown() {
        long userId = 1L;

        when(userRepository.findById(userId))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemRequestService.findAll(userId, 0, 50));

        verify(userRepository).findById(userId);
        verify(itemRequestRepository, never()).findAllByAuthor(any());
    }

    @Test
    void findAll_whenUserExists_thenListOfItemRequestReturned() {
        long userId = 1L;
        int from = 0;
        int size = 50;

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));
        when(itemRequestRepository.findAllByAuthorNotOrderByCreated(any(), any()))
                .thenReturn(new PageImpl<>(List.of(itemRequest)));

        itemRequestService.findAll(userId, from, size);

        verify(userRepository).findById(userId);
        verify(itemRequestRepository).findAllByAuthorNotOrderByCreated(any(), any());
    }

    @Test
    void findAll_whenUserNotExists_thenNotFoundExceptionThrown() {
        long userId = 1L;
        int from = 0;
        int size = 50;

        when(userRepository.findById(userId))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemRequestService.findAll(userId, from, size));

        verify(userRepository).findById(userId);
        verify(itemRequestRepository, never()).findAllByAuthorNotOrderByCreated(any(), any());
    }

    @Test
    void findById_whenUserAndItemRequestExist_thenItemRequestReturned() {
        long userId = 1L;
        long requestId = 1L;

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));
        when(itemRequestRepository.findById(requestId))
                .thenReturn(Optional.of(itemRequest));

        itemRequestService.findById(userId, requestId);

        verify(userRepository).findById(userId);
        verify(itemRequestRepository).findById(requestId);
    }

    @Test
    void findById_whenUserNotExists_thenNotFoundExceptionThrown() {
        long userId = 0L;
        long requestId = 1L;

        when(userRepository.findById(userId))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemRequestService.findById(userId, requestId));

        verify(userRepository).findById(userId);
        verify(itemRequestRepository, never()).findById(requestId);
    }

    @Test
    void findById_whenItemRequestNotExists_thenNotFoundExceptionThrown() {
        long userId = 1L;
        long requestId = 1L;

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));
        when(itemRequestRepository.findById(requestId))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemRequestService.findById(userId, requestId));

        verify(userRepository).findById(userId);
        verify(itemRequestRepository).findById(requestId);
    }
}