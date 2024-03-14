package ru.practicum.shareit.gateway.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.gateway.common.BaseClient;
import ru.practicum.shareit.server.request.dto.ItemRequestCreateRequest;

import java.util.Map;

@Service
public class ItemRequestClient extends BaseClient {
    private static final String API_PREFIX = "/requests";

    @Autowired
    public ItemRequestClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> create(long userId, ItemRequestCreateRequest itemRequestCreateRequest) {
        return post("", userId, itemRequestCreateRequest);
    }

    public ResponseEntity<Object> findAllByAuthor(long authorId) {
        return get("", authorId);
    }

    public ResponseEntity<Object> findAll(long userId, int from, int size) {
        return get("/all?from={from}&size={size}", userId, Map.of("from", from, "size", size));
    }

    public ResponseEntity<Object> findById(long userId, long requestId) {
        return get("/{requestId}", userId, Map.of("requestId", requestId));
    }
}
