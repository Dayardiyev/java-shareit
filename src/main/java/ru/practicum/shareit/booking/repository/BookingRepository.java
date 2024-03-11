package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    Page<Booking> findByBookerIdAndItemIdAndStatusIsAndStartIsBefore(long bookerId, long userId, Status status, LocalDateTime time, Pageable pageable);

    Page<Booking> findAllByBookerIdOrderByStartDesc(long bookerId, Pageable pageable);

    Page<Booking> findAllByBookerIdAndStartIsAfterOrderByStartDesc(long bookerId, LocalDateTime currentTime, Pageable pageable);

    Page<Booking> findAllByBookerIdAndEndIsBeforeOrderByStartDesc(long bookerId, LocalDateTime currentTime, Pageable pageable);

    @Query("select b from Booking b " +
            "where b.booker.id = :bookerId " +
            "and b.start < :currentTime " +
            "and b.end > :currentTime " +
            "order by b.start desc")
    Page<Booking> findAllByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(long bookerId, LocalDateTime currentTime, Pageable pageable);

    Page<Booking> findAllByBookerIdAndStatusIs(long bookerId, Status status, Pageable pageable);

    Optional<Booking> findByIdAndBookerId(long bookingId, long userId);

    Optional<Booking> findByIdAndItemOwnerIdOrderByStartDesc(long bookingId, long ownerId);

    Page<Booking> findAllByItemOwnerIdOrderByStartDesc(long ownerId, Pageable pageable);

    Page<Booking> findAllByItemOwnerIdAndStartIsAfterOrderByStartDesc(long ownerId, LocalDateTime currentTime, Pageable pageable);

    Page<Booking> findAllByItemOwnerIdAndEndIsBeforeOrderByStartDesc(long ownerId, LocalDateTime currentTime, Pageable pageable);

    @Query("select b from Booking b " +
            "where b.item.owner.id = :ownerId " +
            "and b.start < :currentTime " +
            "and b.end > :currentTime " +
            "order by b.start desc")
    Page<Booking> findAllByOwnerIdCurrent(long ownerId, LocalDateTime currentTime, Pageable pageable);

    Page<Booking> findAllByItemOwnerIdAndStatusIs(long ownerId, Status status, Pageable pageable);

    Page<Booking> findAllByItemId(long itemId, Pageable pageable);
}

