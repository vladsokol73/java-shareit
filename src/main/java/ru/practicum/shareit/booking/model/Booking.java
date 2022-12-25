package ru.practicum.shareit.booking.model;

import lombok.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Table(name = "bookings")
@Entity
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@RequiredArgsConstructor
@AllArgsConstructor
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NonNull
    private LocalDateTime startDate;

    @NonNull
    private LocalDateTime endDate;

    @NonNull
    @ManyToOne
    private Item item;

    @NonNull
    @ManyToOne
    private User booker;

    @Enumerated(value = EnumType.STRING)
    private BookingStatus status;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Booking booking = (Booking) o;
        return  startDate.equals(booking.startDate) &&
                endDate.equals(booking.endDate) &&
                item.equals(booking.item) &&
                booker.equals(booking.booker) &&
                status == booking.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(startDate, endDate, item, booker, status);
    }
}
