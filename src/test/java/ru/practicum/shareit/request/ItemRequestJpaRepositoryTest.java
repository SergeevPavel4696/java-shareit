package ru.practicum.shareit.request;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestJpaRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserJpaRepository;
import ru.practicum.shareit.util.PageableMaker;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
public class ItemRequestJpaRepositoryTest {
    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private ItemRequestJpaRepository itemRequestJpaRepository;
    @Autowired
    private UserJpaRepository userJpaRepository;
    private User user;
    private ItemRequest itemRequest1;
    private ItemRequest itemRequest2;
    private Pageable pageable;

    @BeforeEach
    public void beforeEach() {
        user = userJpaRepository.save(new User(1, "Имя", "user@mail.ya"));
        entityManager.persist(user);
        itemRequest1 = itemRequestJpaRepository.save(new ItemRequest(1, "Описание 1", user, LocalDateTime.now()));
        entityManager.persist(itemRequest1);
        itemRequest2 = itemRequestJpaRepository.save(new ItemRequest(2, "Описание 2", user, LocalDateTime.now()));
        entityManager.persist(itemRequest2);
        entityManager.getEntityManager().getTransaction().commit();
        pageable = PageableMaker.makePage(0, 10);
    }

    @AfterEach
    public void afterEach() {
        userJpaRepository.deleteAll();
        itemRequestJpaRepository.deleteAll();
    }

    @Test
    public void findTest() {
        assertEquals(itemRequest1, itemRequestJpaRepository.findById(1));
        assertEquals(itemRequest2, itemRequestJpaRepository.findById(2));
    }

    @Test
    public void findAllByRequesterIdTest() {
        assertEquals(List.of(itemRequest1, itemRequest2), itemRequestJpaRepository.findAllByRequesterId(1));
        assertEquals(List.of(), itemRequestJpaRepository.findAllByRequesterId(2));
    }

    @Test
    public void findAllByRequesterIdNotOrderByCreatedDesc() {
        assertTrue(itemRequestJpaRepository.findAllByRequesterIdNotOrderByCreatedDesc(2, pageable).contains(itemRequest1));
        assertTrue(itemRequestJpaRepository.findAllByRequesterIdNotOrderByCreatedDesc(2, pageable).contains(itemRequest2));
        assertEquals(2, itemRequestJpaRepository.findAllByRequesterIdNotOrderByCreatedDesc(2, pageable).size());
        pageable = PageableMaker.makePage(0, 1);
        assertEquals(1, itemRequestJpaRepository.findAllByRequesterIdNotOrderByCreatedDesc(2, pageable).size());
    }
}
