package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.Duplicate;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.model.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequestDtoResponse;
import ru.practicum.shareit.request.model.ItemRequestDtoWithItems;
import ru.practicum.shareit.request.repository.ItemRequestJpaRepository;
import ru.practicum.shareit.request.validator.ItemRequestIdValidator;
import ru.practicum.shareit.request.validator.ItemRequestValidator;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.validator.UserIdValidator;
import ru.practicum.shareit.util.PageableMaker;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemRequestService {

    private final UserService userService;
    private final ItemService itemService;
    private final ItemRequestJpaRepository itemRequestJpaRepository;

    public ItemRequestDtoResponse create(ItemRequestDto itemRequestDto, Integer requesterId) {
        ItemRequestValidator.validate(itemRequestDto);
        User requester = userService.getUser(requesterId);
        UserIdValidator.validate(userService.get(requesterId));
        ItemRequest itemRequest = ItemRequestMapper.convert(itemRequestDto, requester, LocalDateTime.now());
        ItemRequestValidator.validate(itemRequest);
        try {
            return ItemRequestMapper.convertToResponse(itemRequestJpaRepository.save(itemRequest));
        } catch (DataIntegrityViolationException e) {
            throw new Duplicate("Запрос с указанными данными уже существует.");
        }
    }

    public ItemRequestDtoWithItems get(int itemRequestId, int userId) {
        ItemRequestIdValidator.validate(getItemRequest(itemRequestId));
        UserIdValidator.validate(userService.get(userId));
        ItemRequest itemRequest = itemRequestJpaRepository.findById(itemRequestId);
        return ItemRequestMapper.convert(itemRequest, itemService.getAllByItemRequestId(itemRequestId));
    }

    public ItemRequest getItemRequest(int itemRequestId) {
        ItemRequest itemRequest = itemRequestJpaRepository.findById(itemRequestId);
        ItemRequestIdValidator.validate(itemRequest);
        return itemRequest;
    }

    public List<ItemRequestDtoWithItems> getAllByRequesterId(Integer requesterId) {
        UserIdValidator.validate(userService.get(requesterId));
        List<ItemRequest> itemRequests = itemRequestJpaRepository.findAllByRequesterId(requesterId);
        List<ItemRequestDtoWithItems> itemRequestDtoWithItems = new ArrayList<>();
        for (ItemRequest itemRequest : itemRequests) {
            itemRequestDtoWithItems.add(ItemRequestMapper.convert(itemRequest, itemService.getAllByItemRequestId(itemRequest.getId())));
        }
        return itemRequestDtoWithItems;
    }

    public List<ItemRequestDtoWithItems> getAllByOtherRequester(Integer myId, Integer from, Integer size) {
        UserIdValidator.validate(userService.get(myId));
        List<ItemRequest> itemRequests;
        try {
            itemRequests = itemRequestJpaRepository.findAllByRequesterIdNotOrderByCreatedDesc(myId, PageableMaker.makePage(from, size));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Неверные параметры пагинации.");
        }

        List<ItemRequestDtoWithItems> itemRequestDtoWithItems = new ArrayList<>();
        for (ItemRequest itemRequest : itemRequests) {
            itemRequestDtoWithItems.add(ItemRequestMapper
                    .convert(itemRequest, itemService.getAllByItemRequestId(itemRequest.getId())));
        }
        return itemRequestDtoWithItems;
    }
}
