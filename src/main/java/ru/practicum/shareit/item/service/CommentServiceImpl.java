package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.common.exception.BadRequestException;
import ru.practicum.shareit.item.dto.CommentCreateRequest;
import ru.practicum.shareit.item.dto.CommentMapper;
import ru.practicum.shareit.item.dto.CommentResponse;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;
    private final CommentMapper mapper;

    @Override
    public CommentResponse create(long userId, long itemId, CommentCreateRequest commentCreateRequest) {

        LocalDateTime time = LocalDateTime.now();

        Booking booking = bookingRepository
                .findByBookerIdAndItemIdAndStatusIsAndStartIsBefore(userId, itemId, Status.APPROVED, time)
                .stream()
                .findFirst()
                .orElseThrow(() -> new BadRequestException("Комментарий не создан, не найден бронь"));

        User user = booking.getBooker();
        Item item = booking.getItem();

        Comment comment = mapper.mapFromCreateRequestDto(commentCreateRequest);
        comment.setItem(item);
        comment.setAuthor(user);

        return mapper.mapToResponseEntity(commentRepository.save(comment));
    }
}
