package ru.practicum.shareit.item.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Repository
public interface ItemJpaRepository extends JpaRepository<Item, Integer> {

    Item findById(int itemId);

    List<Item> findAllByOwner(User owner, Pageable pageable);

    @Query("SELECT i FROM Item i WHERE (UPPER(i.name) LIKE UPPER(CONCAT('%',:text,'%')) OR " +
            "UPPER(i.description) LIKE UPPER(CONCAT('%',:text,'%')) AND i.available = TRUE)")
    List<Item> findItemsContainingText(@Param("text") String text, Pageable pageable);

    List<Item> findAllByRequestId(Integer requestId);

}
