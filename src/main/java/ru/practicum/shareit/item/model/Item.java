package ru.practicum.shareit.item.model;

import lombok.*;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Table(name = "items")
@Entity
@Setter
@Getter
@ToString
@Builder
@NoArgsConstructor
@RequiredArgsConstructor
@AllArgsConstructor
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NonNull
    private String name;

    @NonNull
    private String description;

    @NonNull
    private Boolean available;

    @NonNull
    @ManyToOne
    private User owner;

    @ManyToOne
    private ItemRequest request;

    @OneToMany(mappedBy = "item",
               orphanRemoval = true,
               cascade = CascadeType.REMOVE)
    @OrderBy("startDate ASC")
    @Singular
    @ToString.Exclude
    private List<Booking> bookings = new ArrayList<>();

    @OneToMany(mappedBy = "item",
            orphanRemoval = true,
            cascade = CascadeType.REMOVE)
    @Singular
    @ToString.Exclude
    private List<Comment> comments = new ArrayList<>();

    @Transient
    @ToString.Exclude
    private Booking lastBooking;

    @Transient
    @ToString.Exclude
    private Booking nextBooking;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        return  name.equals(item.name) &&
                description.equals(item.description) &&
                available.equals(item.available) &&
                owner.equals(item.owner) &&
                Objects.equals(request, item.request);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, available, owner, request);
    }
}
