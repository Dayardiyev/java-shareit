package ru.practicum.shareit.gateway.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.server.user.dto.UserCreateRequest;
import ru.practicum.shareit.server.user.dto.UserUpdateRequest;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import static ru.practicum.shareit.server.common.Constants.DEFAULT_FROM;
import static ru.practicum.shareit.server.common.Constants.DEFAULT_SIZE;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/users")
public class UserController {

    private final UserClient client;

    @GetMapping
    public ResponseEntity<Object> findAll(
            @PositiveOrZero @RequestParam(defaultValue = DEFAULT_FROM) int from,
            @Positive @RequestParam(defaultValue = DEFAULT_SIZE) int size
    ) {
        return client.findAll(from, size);
    }

    @GetMapping("{id}")
    public ResponseEntity<Object> findById(@PathVariable long id) {
        return client.findById(id);
    }

    @PostMapping
    public ResponseEntity<Object> create(@RequestBody @Valid UserCreateRequest userCreateRequest) {
        return client.create(userCreateRequest);
    }

    @PatchMapping("{id}")
    public ResponseEntity<Object> update(@PathVariable long id, @RequestBody @Valid UserUpdateRequest userUpdateRequest) {
        return client.update(id, userUpdateRequest);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Object> delete(@PathVariable long id) {
        return client.delete(id);
    }
}
