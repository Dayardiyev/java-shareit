package ru.practicum.shareit.gateway.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.gateway.common.BaseClient;
import ru.practicum.shareit.server.item.dto.CommentCreateRequest;
import ru.practicum.shareit.server.item.dto.ItemCreateRequest;
import ru.practicum.shareit.server.item.dto.ItemUpdateRequest;

import java.util.Map;

@Service
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> create(long userId, ItemCreateRequest itemCreateRequest) {
        return post("", userId, itemCreateRequest);
    }

    public ResponseEntity<Object> update(long userId, long itemId, ItemUpdateRequest itemUpdateRequest) {
        return patch("/{itemId}", userId, Map.of("itemId", itemId), itemUpdateRequest);
    }

    public ResponseEntity<Object> findAllByName(String text, int from, int size) {
        Map<String, Object> parameters = Map.of(
                "text", text,
                "from", from,
                "size", size
        );

        return get("/search?text={text}&from={from}&size={size}", null, parameters);
    }

    public ResponseEntity<Object> findAllByUserId(long userId, int from, int size) {
        Map<String, Object> parameters = Map.of(
                "from", from,
                "size", size
        );

        return get("?from={from}&size={size}", userId, parameters);
    }

    public ResponseEntity<Object> findById(long userId, long itemId) {
        return get("/{itemId}", userId, Map.of("itemId", itemId));
    }

    public ResponseEntity<Object> addComment(long userId, long itemId, CommentCreateRequest commentCreateRequest) {
        return post("/{itemId}/comment", userId, Map.of("itemId", itemId), commentCreateRequest);
    }
}
