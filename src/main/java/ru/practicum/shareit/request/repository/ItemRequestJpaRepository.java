package ru.practicum.shareit.request.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

@Repository
public interface ItemRequestJpaRepository extends JpaRepository<ItemRequest, Integer> {

    ItemRequest findById(int itemRequestId);

    List<ItemRequest> findAllByRequesterId(int requesterId);

    List<ItemRequest> findAllByRequesterIdNotOrderByCreatedDesc(Integer userId, Pageable pageable);

}
