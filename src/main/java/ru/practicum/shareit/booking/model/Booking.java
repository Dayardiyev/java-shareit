package ru.practicum.shareit.booking.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.common.AbstractEntity;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "bookings")
public class Booking extends AbstractEntity {

    @ManyToOne
    @JoinColumn(name = "booker_id")
    User booker;

    @ManyToOne
    @JoinColumn(name = "item_id")
    Item item;

    @Column(name = "start_time")
    LocalDateTime start;

    @Column(name = "end_time")
    LocalDateTime end;

    @Enumerated(value = EnumType.STRING)
    Status status;
}
