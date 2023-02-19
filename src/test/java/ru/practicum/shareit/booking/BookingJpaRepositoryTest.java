package ru.practicum.shareit.booking;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingJpaRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemJpaRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserJpaRepository;
import ru.practicum.shareit.util.PageableMaker;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.practicum.shareit.booking.model.BookingStatus.WAITING;

@DataJpaTest
public class BookingJpaRepositoryTest {
    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private BookingJpaRepository bookingJpaRepository;
    @Autowired
    private UserJpaRepository userJpaRepository;
    @Autowired
    private ItemJpaRepository itemJpaRepository;
    private Booking booking1;
    private Booking booking2;
    private Booking booking3;
    private Booking booking4;
    private Pageable pageable;

    @BeforeEach
    public void beforeEach() {
        User user1 = userJpaRepository.save(new User(1, "Имя1", "1@mail.ya"));
        entityManager.persist(user1);
        User user2 = userJpaRepository.save(new User(2, "Имя 2", "2@mail.ya"));
        entityManager.persist(user2);
        Item item1 = itemJpaRepository.save(new Item(1, "Название 1", "Описание 1", true, user2, null));
        entityManager.persist(item1);
        Item item2 = itemJpaRepository.save(new Item(2, "Название 2", "Описание 2", true, user2, null));
        entityManager.persist(item2);
        booking1 = bookingJpaRepository.save(new Booking(1,
                LocalDateTime.of(2024, 1, 1, 0, 0, 0),
                LocalDateTime.of(2024, 1, 5, 0, 0, 0),
                item1, user1, WAITING));
        entityManager.persist(booking1);
        booking2 = bookingJpaRepository.save(new Booking(2,
                LocalDateTime.of(2024, 1, 1, 0, 0, 0),
                LocalDateTime.of(2024, 1, 5, 0, 0, 0),
                item2, user1, WAITING));
        entityManager.persist(booking2);
        booking3 = bookingJpaRepository.save(new Booking(3,
                LocalDateTime.of(2024, 2, 1, 0, 0, 0),
                LocalDateTime.of(2024, 2, 5, 0, 0, 0),
                item2, user1, WAITING));
        entityManager.persist(booking3);
        booking4 = bookingJpaRepository.save(new Booking(4,
                LocalDateTime.of(2024, 3, 1, 0, 0, 0),
                LocalDateTime.of(2024, 3, 5, 0, 0, 0),
                item2, user1, WAITING));
        entityManager.persist(booking4);
        entityManager.getEntityManager().getTransaction().commit();
        pageable = PageableMaker.makePage(0, 10);
    }

    @AfterEach
    public void afterEach() {
        bookingJpaRepository.deleteAll();
        userJpaRepository.deleteAll();
        itemJpaRepository.deleteAll();
    }


    @Test
    public void findTest() {
        assertEquals(booking1, bookingJpaRepository.findById(1));
        assertEquals(booking2, bookingJpaRepository.findById(2));
        assertEquals(booking3, bookingJpaRepository.findById(3));
        assertEquals(booking4, bookingJpaRepository.findById(4));
    }

    @Test
    public void findAllByBookerIdOrderByStartDescTest() {
        assertEquals(4, bookingJpaRepository.findAllByBooker_IdOrderByStartDesc(1, pageable).size());
        assertEquals(0, bookingJpaRepository.findAllByBooker_IdOrderByStartDesc(2, pageable).size());
    }

    @Test
    public void findAllByItemOwnerIdOrderByStartDescTest() {
        assertEquals(0, bookingJpaRepository.findAllByItem_Owner_IdOrderByStartDesc(1, pageable).size());
        assertEquals(4, bookingJpaRepository.findAllByItem_Owner_IdOrderByStartDesc(2, pageable).size());
    }
}
