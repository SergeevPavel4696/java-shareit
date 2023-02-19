package ru.practicum.shareit.user;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserJpaRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
public class UserJpaRepositoryTest {
    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private UserJpaRepository userJpaRepository;
    private User user;

    @BeforeEach
    public void beforeEach() {
        user = userJpaRepository.save(new User(1, "Имя", "user@mail.ya"));
        entityManager.persist(user);
        entityManager.getEntityManager().getTransaction().commit();
    }

    @AfterEach
    public void afterEach() {
        userJpaRepository.deleteAll();
    }

    @Test
    public void findTest() {
        assertEquals("user@mail.ya", userJpaRepository.findById(1).getEmail());
        assertEquals("Имя", userJpaRepository.findById(1).getName());
    }
}
