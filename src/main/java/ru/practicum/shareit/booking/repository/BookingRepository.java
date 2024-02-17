package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByBookerIdAndItemIdAndStatusIsAndStartIsBefore(long bookerId, long userId, Status status, LocalDateTime time);

    List<Booking> findAllByBookerIdOrderByStartDesc(long bookerId);

    List<Booking> findAllByBookerIdAndStartIsAfterOrderByStartDesc(long bookerId, LocalDateTime currentTime);

    List<Booking> findAllByBookerIdAndEndIsBeforeOrderByStartDesc(long bookerId, LocalDateTime currentTime);

    @Query("select b from Booking b " +
            "where b.booker.id = :bookerId " +
            "and b.start < :currentTime " +
            "and b.end > :currentTime " +
            "order by b.start desc")
    List<Booking> findAllByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(long bookerId, LocalDateTime currentTime);

    List<Booking> findAllByBookerIdAndStatusIs(long bookerId, Status status);

    Optional<Booking> findByIdAndBookerId(long bookingId, long userId);

    Optional<Booking> findByIdAndItemOwnerIdOrderByStartDesc(long bookingId, long ownerId);

    List<Booking> findAllByItemOwnerIdOrderByStartDesc(long ownerId);

    List<Booking> findAllByItemOwnerIdAndStartIsAfterOrderByStartDesc(long ownerId, LocalDateTime currentTime);

    List<Booking> findAllByItemOwnerIdAndEndIsBeforeOrderByStartDesc(long ownerId, LocalDateTime currentTime);

    @Query("select b from Booking b " +
            "where b.item.owner.id = :ownerId " +
            "and b.start < :currentTime " +
            "and b.end > :currentTime " +
            "order by b.start desc")
    List<Booking> findAllByOwnerIdCurrent(long ownerId, LocalDateTime currentTime);

    List<Booking> findAllByItemOwnerIdAndStatusIs(long ownerId, Status status);
}

