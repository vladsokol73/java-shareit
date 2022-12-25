package ru.practicum.shareit.item.model;

import lombok.Data;
import lombok.ToString;
import ru.practicum.shareit.booking.Booking;

import javax.persistence.*;

@Data
@Entity
@Table(name = "items")
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    private String description;

    private Boolean available;

    private Integer owner;

    @Column(name = "request")
    private Integer requestId;

    @Transient
    @ToString.Exclude
    private Booking lastBooking;

    @Transient
    @ToString.Exclude
    private Booking nextBooking;
}
