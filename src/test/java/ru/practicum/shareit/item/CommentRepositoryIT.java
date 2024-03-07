package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import ru.practicum.shareit.common.ObjectGenerator;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
@Import(ObjectGenerator.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class CommentRepositoryIT {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final ObjectGenerator objectGenerator;

    private Comment comment;

    @BeforeEach
    void setUp() {
        User owner = objectGenerator.next(User.class);
        User author = objectGenerator.next(User.class);
        Item item = objectGenerator.next(Item.class);
        comment = objectGenerator.next(Comment.class);

        item.setOwner(owner);
        comment.setItem(item);
        comment.setAuthor(author);

        userRepository.save(owner);
        userRepository.save(author);
        itemRepository.save(item);
    }

    @Test
    void save_whenAuthorAndTextAndItemExists_thenCommentSaved() {
        assertDoesNotThrow(() -> commentRepository.save(comment));
    }

    @Test
    void save_whenAuthorIsNull_thenDataIntegrityViolationExceptionThrown() {
        comment.setAuthor(null);

        assertThrows(DataIntegrityViolationException.class, () -> commentRepository.save(comment));
    }

    @Test
    void save_whenTextIsNull_thenDataIntegrityViolationExceptionThrown() {
        comment.setText(null);

        assertThrows(DataIntegrityViolationException.class, () -> commentRepository.save(comment));

    }

    @Test
    void save_whenItemIsNull_thenDataIntegrityViolationExceptionThrown() {
        comment.setItem(null);

        assertThrows(DataIntegrityViolationException.class, () -> commentRepository.save(comment));
    }
}