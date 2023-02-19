package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

@Repository
public interface BookingJpaRepository extends JpaRepository<Booking, Integer> {

    Booking findById(int bookingId);

    List<Booking> findAllByBooker_IdOrderByStartDesc(int bookerId, Pageable pageable);

    List<Booking> findAllByItem_Owner_IdOrderByStartDesc(int ownerId, Pageable pageable);
}
