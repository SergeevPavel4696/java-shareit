//package ru.practicum.shareit.user;
//
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
//import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
//import ru.practicum.shareit.user.dao.UserRepository;
//import ru.practicum.shareit.user.model.User;
//
//@DataJpaTest
//public class UserRepositoryTest {
//    @Autowired
//    private TestEntityManager entityManager;
//    @Autowired
//    private UserRepository userRepository;
//    private User user;
//
//    @BeforeEach
//    public void beforeEach() {
//        user = userRepository.save(new User(1L, "Имя", "email@email.com"));
//        entityManager.persist(user);
//        entityManager.getEntityManager().getTransaction().commit();
//    }
//
//    @AfterEach
//    public void afterEach() {
//        userRepository.deleteAll();
//    }
//
//    @Test
//    public void existsByEmailTest() {
//        assertTrue(userRepository.existsByEmail("email@email.com"));
//    }
//}
