package ru.practicum.shareit.request.model;

import lombok.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "requests")
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@RequiredArgsConstructor
@AllArgsConstructor
public class ItemRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NonNull
    private String description;

    private LocalDateTime created;

    @NonNull
    @ManyToOne
    private User requestor;

    @OneToMany(mappedBy = "request",
            orphanRemoval = true,
            cascade = CascadeType.REMOVE)
    @Singular
    @ToString.Exclude
    private List<Item> items = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        created = LocalDateTime.now().withNano(0);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItemRequest request = (ItemRequest) o;
        return  description.equals(request.description) &&
                requestor.equals(request.requestor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(description, requestor);
    }
}
