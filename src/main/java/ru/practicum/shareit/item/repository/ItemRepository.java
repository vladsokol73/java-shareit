package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;


@Repository
public interface ItemRepository extends JpaRepository<Item, Integer> {

    @Query("select i from Item i where i.owner.id = :ownerId order by i.id")
    List<Item> findByOwner(@Param("ownerId") int ownerId);

    @Query("select i from Item i where " +
            "i.available = true and ( " +
            "lower(i.name) LIKE LOWER('%' || :pattern || '%') or " +
            "lower(i.description) LIKE LOWER('%' || :pattern || '%'))")
    List<Item> findAvailableItemsByNameOrDescription(String pattern);
}
