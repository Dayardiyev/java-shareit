package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findAllByOwnerIdOrderById(Long ownerId);

    @Query(value = "select i from Item i " +
            "where length(:text) > 0 " +
            "and (lower(i.name) like concat('%', lower(:text), '%') " +
            "or lower(i.description) like concat('%', lower(:text), '%')) " +
            "and i.available = true")
    List<Item> findAllByNameContainingIgnoreCase(String text);

    Optional<Item> findByIdAndOwnerId(Long id, Long ownerId);
}
