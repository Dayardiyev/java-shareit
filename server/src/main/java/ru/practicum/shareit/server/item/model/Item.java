package ru.practicum.shareit.server.item.model;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.JoinFormula;
import ru.practicum.shareit.server.booking.model.Booking;
import ru.practicum.shareit.server.common.AbstractEntity;
import ru.practicum.shareit.server.user.model.User;

import javax.persistence.*;
import java.util.Set;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "items")
public class Item extends AbstractEntity {

    String name;

    String description;

    Boolean available;

    @Column(name = "request_id")
    Long requestId;

    @ManyToOne(fetch = FetchType.LAZY)
    User owner;

    @OneToMany(mappedBy = "item")
    Set<Comment> comments;

    @ManyToOne
    @JoinFormula(
            "(select b.id from bookings b " +
                    "where b.item_id = id " +
                    "and b.start_time < localtimestamp(6) " +
                    "and b.status = 'APPROVED' " +
                    "order by b.start_time desc limit 1)")
    Booking lastBooking;

    @ManyToOne
    @JoinFormula(
            "(select b.id from bookings b " +
                    "where b.item_id = id " +
                    "and b.start_time > localtimestamp(6) " +
                    "and b.status = 'APPROVED' " +
                    "order by b.start_time limit 1)")
    Booking nextBooking;
}
