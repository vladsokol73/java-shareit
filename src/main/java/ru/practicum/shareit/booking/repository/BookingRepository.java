package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.util.List;


@Repository
public interface BookingRepository extends JpaRepository<Booking, Integer> {

    @Query("SELECT b FROM Booking b WHERE b.booker.id = :bookerId")
    List<Booking> getBookingByBooker(@Param("bookerId") int bookerId, Pageable pageable);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = :bookerId AND b.status = :status")
    List<Booking> getBookingByBookerAndStatus(@Param("bookerId") int bookerId, @Param("status") BookingStatus status, Pageable pageable);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = :bookerId AND b.endDate < NOW()")
    List<Booking> getPastBookingByBooker(@Param("bookerId") int bookerId, Pageable pageable);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = :bookerId AND b.startDate <= NOW() AND b.endDate >= NOW()")
    List<Booking> getCurrentBookingByBooker(@Param("bookerId") int bookerId, Pageable pageable);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = :bookerId AND b.startDate > NOW()")
    List<Booking> getFutureBookingByBooker(@Param("bookerId") int bookerId, Pageable pageable);

    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = :ownerId")
    List<Booking> getBookingByOwner(@Param("ownerId") int ownerId, Pageable pageable);

    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = :ownerId AND b.status = :status")
    List<Booking> getBookingByOwnerAndStatus(@Param("ownerId") int ownerId, @Param("status") BookingStatus status, Pageable pageable);

    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = :ownerId AND b.endDate < NOW()")
    List<Booking> getPastBookingByOwner(@Param("ownerId") int ownerId, Pageable pageable);

    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = :ownerId AND b.startDate <= NOW() AND b.endDate >= NOW()")
    List<Booking> getCurrentBookingByOwner(@Param("ownerId") int ownerId, Pageable pageable);

    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = :ownerId AND b.startDate > NOW()")
    List<Booking> getFutureBookingByOwner(@Param("ownerId") int ownerId, Pageable pageable);
}