package ru.practicum.shareit.item;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemJpaRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestJpaRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserJpaRepository;
import ru.practicum.shareit.util.PageableMaker;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
public class ItemJpaRepositoryTest {
    @Autowired
    private TestEntityManager testEntityManager;
    @Autowired
    private ItemJpaRepository itemJpaRepository;
    @Autowired
    private UserJpaRepository userJpaRepository;
    @Autowired
    private ItemRequestJpaRepository itemRequestJpaRepository;
    private Pageable pageable;
    private User owner1;
    private User owner2;
    private Item item1;
    private Item item2;
    private Item item3;
    private ItemRequest request1;
    private ItemRequest request2;

    @BeforeEach
    public void beforeEach() {
        owner1 = userJpaRepository.save(new User(1, "Имя1", "owner1@mail.ya"));
        testEntityManager.persist(owner1);
        owner2 = userJpaRepository.save(new User(2, "Имя2", "owner2@mail.ya"));
        testEntityManager.persist(owner2);
        request1 = itemRequestJpaRepository.save(new ItemRequest(1, "Запрос первой вещи", owner1, LocalDateTime.now()));
        testEntityManager.persist(request1);
        request2 = itemRequestJpaRepository.save(new ItemRequest(2, "Запрос второй вещи", owner1, LocalDateTime.now()));
        testEntityManager.persist(request2);
        item1 = itemJpaRepository.save(new Item(1, "Название 1", "Описание 1", true, owner1, 1));
        testEntityManager.persist(item1);
        item2 = itemJpaRepository.save(new Item(2, "Название 2", "Описание 2", true, owner1, 1));
        testEntityManager.persist(item2);
        item3 = itemJpaRepository.save(new Item(3, "Название 3", "Описание 3", true, owner2, 2));
        testEntityManager.persist(item3);
        testEntityManager.getEntityManager().getTransaction().commit();
        pageable = PageableMaker.makePage(0, 10);
    }

    @AfterEach
    public void afterEach() {
        itemJpaRepository.deleteAll();
        userJpaRepository.deleteAll();
        itemRequestJpaRepository.deleteAll();
    }

    @Test
    public void findAllByOwnerTest() {
        List<Item> items = itemJpaRepository.findAllByOwner(owner1, pageable);
        assertEquals(2, items.size());
    }

    @Test
    public void findAllTest() {
        List<Item> items = itemJpaRepository.findAllByRequestId(1);
        assertEquals(2, items.size());
    }

    @Test
    public void findByTextTest() {
        List<Item> items1 = itemJpaRepository.findItemsContainingText("нИЕ 1", pageable);
        List<Item> items2 = itemJpaRepository.findItemsContainingText("ОпИс", pageable);
        assertEquals(1, items1.size());
        assertEquals(3, items2.size());
    }
}
