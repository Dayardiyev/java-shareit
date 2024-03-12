package ru.practicum.shareit.gateway.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.gateway.common.BaseClient;
import ru.practicum.shareit.server.user.dto.UserCreateRequest;
import ru.practicum.shareit.server.user.dto.UserUpdateRequest;

import java.util.Map;

@Service
public class UserClient extends BaseClient {
    private static final String API_PREFIX = "/users";

    @Autowired
    public UserClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> findAll(int from, int size) {
        return get("", null, Map.of("from", from, "size", size));
    }

    public ResponseEntity<Object> findById(long id) {
        return get("/{id}", null, Map.of("id", Long.toString(id)));
    }

    public ResponseEntity<Object> create(UserCreateRequest userCreateRequest) {
        return post("", userCreateRequest);
    }

    public ResponseEntity<Object> update(long id, UserUpdateRequest userUpdateRequest) {
        return patch("/{id}", null, Map.of("id", Long.toString(id)), userUpdateRequest);
    }

    public ResponseEntity<Object> delete(long id) {
        return delete("/{id}", null, Map.of("id", Long.toString(id)));
    }
}