package ru.practicum.shareit.item.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {

    Page<Item> findAllByOwnerIdOrderById(Long ownerId, Pageable pageable);

    @Query(value = "select i from Item i " +
            "where length(:text) > 0 " +
            "and (lower(i.name) like concat('%', lower(:text), '%') " +
            "or lower(i.description) like concat('%', lower(:text), '%')) " +
            "and i.available = true")
    Page<Item> findAllByNameContainingIgnoreCase(String text, Pageable pageable);

    Optional<Item> findByIdAndOwnerId(Long id, Long ownerId);
}
