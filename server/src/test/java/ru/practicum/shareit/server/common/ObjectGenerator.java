package ru.practicum.shareit.server.common;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.server.booking.model.Booking;
import ru.practicum.shareit.server.booking.model.Status;
import ru.practicum.shareit.server.booking.dto.BookingCreateRequest;
import ru.practicum.shareit.server.item.model.Comment;
import ru.practicum.shareit.server.item.model.Item;
import ru.practicum.shareit.server.item.dto.CommentCreateRequest;
import ru.practicum.shareit.server.item.dto.ItemCreateRequest;
import ru.practicum.shareit.server.item.dto.ItemUpdateRequest;
import ru.practicum.shareit.server.request.model.ItemRequest;
import ru.practicum.shareit.server.request.dto.ItemRequestCreateRequest;
import ru.practicum.shareit.server.user.model.User;
import ru.practicum.shareit.server.user.dto.UserCreateRequest;
import ru.practicum.shareit.server.user.dto.UserResponse;
import ru.practicum.shareit.server.user.dto.UserUpdateRequest;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class ObjectGenerator {

    private final Map<Class<?>, Integer> counters;
    private final Map<Class<?>, Function<Integer, ?>> generators;

    public ObjectGenerator() {
        counters = new HashMap<>();
        generators = new HashMap<>();

        generators.put(UserCreateRequest.class, ObjectGenerator::generateUserCreateRequest);
        generators.put(UserUpdateRequest.class, ObjectGenerator::generateUserUpdateRequest);
        generators.put(UserResponse.class, ObjectGenerator::generateUserResponse);
        generators.put(User.class, ObjectGenerator::generateUser);

        generators.put(ItemRequestCreateRequest.class, ObjectGenerator::generateItemRequestCreateRequest);
        generators.put(ItemRequest.class, ObjectGenerator::generateItemRequest);

        generators.put(ItemCreateRequest.class, ObjectGenerator::generateItemCreateRequest);
        generators.put(ItemUpdateRequest.class, ObjectGenerator::generateItemUpdateRequest);
        generators.put(Item.class, ObjectGenerator::generateItem);

        generators.put(BookingCreateRequest.class, ObjectGenerator::generateBookingCreateRequest);
        generators.put(Booking.class, ObjectGenerator::generateBooking);

        generators.put(CommentCreateRequest.class, ObjectGenerator::generateCommentCreateRequest);
        generators.put(Comment.class, ObjectGenerator::generateComment);
    }

    public <T> T next(Class<T> cls) {
        int index = counters.getOrDefault(cls, 0) + 1;
        counters.put(cls, index);
        return generate(cls, index);
    }

    @SuppressWarnings("unchecked")
    private <T> T generate(Class<T> cls, int index) {
        if (!generators.containsKey(cls)) {
            throw new IllegalArgumentException();
        }
        return (T) generators.get(cls).apply(index);
    }

    private static UserCreateRequest generateUserCreateRequest(int index) {
        UserCreateRequest userCreateRequest = new UserCreateRequest();
        userCreateRequest.setName(String.format("User %d name", index));
        userCreateRequest.setEmail(String.format("users_%d@mail.com", index));
        return userCreateRequest;
    }

    private static UserUpdateRequest generateUserUpdateRequest(int index) {
        UserUpdateRequest userUpdateRequest = new UserUpdateRequest();
        userUpdateRequest.setName(String.format("Updated user %d Name", index));
        userUpdateRequest.setEmail(String.format("updated_user_%d@mail.com", index));
        return userUpdateRequest;
    }

    private static User generateUser(int index) {
        User user = new User();
        user.setName(String.format("User %d name", index));
        user.setEmail(String.format("user_%d@mail.com", index));
        return user;
    }

    private static UserResponse generateUserResponse(int index) {
        UserResponse userResponse = new UserResponse();
        userResponse.setName(String.format("User %d name", index));
        userResponse.setEmail(String.format("users_%d@mail.com", index));
        return userResponse;
    }

    private static ItemRequestCreateRequest generateItemRequestCreateRequest(int index) {
        ItemRequestCreateRequest itemRequestCreateRequest = new ItemRequestCreateRequest();
        itemRequestCreateRequest.setDescription(String.format("Search request %d for an item", index));
        return itemRequestCreateRequest;
    }

    private static ItemRequest generateItemRequest(int index) {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription(String.format("Search request %d for an item", index));
        return itemRequest;
    }

    private static ItemCreateRequest generateItemCreateRequest(int index) {
        ItemCreateRequest itemCreateRequest = new ItemCreateRequest();
        itemCreateRequest.setName(String.format("Item %d name", index));
        itemCreateRequest.setDescription(String.format("Item %d description", index));
        itemCreateRequest.setAvailable(true);
        return itemCreateRequest;
    }

    private static ItemUpdateRequest generateItemUpdateRequest(int index) {
        ItemUpdateRequest itemUpdateRequest = new ItemUpdateRequest();
        itemUpdateRequest.setName(String.format("Updated item %d name", index));
        itemUpdateRequest.setDescription(String.format("Updated item %d description", index));
        itemUpdateRequest.setAvailable(true);
        return itemUpdateRequest;
    }

    private static Item generateItem(int index) {
        Item item = new Item();
        item.setName(String.format("Item %d name", index));
        item.setDescription(String.format("Item %d description", index));
        item.setAvailable(true);
        return item;
    }

    private static BookingCreateRequest generateBookingCreateRequest(int index) {
        return new BookingCreateRequest();
    }

    private static Booking generateBooking(int index) {
        Booking booking = new Booking();
        booking.setStatus(Status.WAITING);
        return booking;
    }

    private static CommentCreateRequest generateCommentCreateRequest(int index) {
        CommentCreateRequest commentCreateRequest = new CommentCreateRequest();
        commentCreateRequest.setText(String.format("Comment %d for an item", index));
        return commentCreateRequest;
    }

    private static Comment generateComment(int index) {
        Comment comment = new Comment();
        comment.setText(String.format("Comment %d for an item", index));
        return comment;
    }
}
