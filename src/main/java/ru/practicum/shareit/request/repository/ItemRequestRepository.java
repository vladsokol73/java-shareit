package ru.practicum.shareit.request.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.Collection;
import java.util.List;

@Repository
public interface ItemRequestRepository extends PagingAndSortingRepository<ItemRequest, Integer> {

    @Query("SELECT ir FROM ItemRequest ir WHERE ir.requestor.id = :requestorId ORDER BY created DESC")
    List<ItemRequest> getItemRequestByRequestor(@Param("requestorId") int requestorId);

    Collection<ItemRequest> findAll();

    Page<ItemRequest> findAllByRequestorIdNot(int requestorId, Pageable pageable);
}

