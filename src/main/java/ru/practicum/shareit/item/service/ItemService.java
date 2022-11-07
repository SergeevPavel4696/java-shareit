package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.IncorrectId;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.item.validator.ItemValidator;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.validator.UserIdValidator;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemStorage itemStorage;
    private final UserService userService;

    public Item create(ItemDto itemDto, Integer ownerId) {
        ItemValidator.validate(itemDto, ownerId);
        Item item = ItemMapper.convert(itemDto, ownerId);
        ItemValidator.validate(item);
        UserIdValidator.validate(userService.getUsersId(), ownerId);
        return itemStorage.create(item);
    }

    public Item update(Integer itemId, Integer ownerId, ItemDto itemDto) {
        ItemValidator.validate(itemDto, ownerId);
        UserIdValidator.validate(userService.getUsersId(), ownerId);
        Item item = get(itemId);
        if (!item.getOwnerId().equals(ownerId)) {
            throw new IncorrectId("Вы не можете редактировать описание чужих вещей.");
        }
        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
        return itemStorage.update(item);
    }

    public Item get(int itemId) {
        return itemStorage.get(itemId);
    }

    public List<Item> getAllByOwnerId(Integer ownerId) {
        return itemStorage.getAllByOwnerId(ownerId);
    }

    public List<Item> searchItemsByText(String text) {
        List<Item> items = new ArrayList<>();
        if (text != null && !text.isEmpty()) {
            items = itemStorage.getAllWantedItem(text);
        }
        return items;
    }
}
