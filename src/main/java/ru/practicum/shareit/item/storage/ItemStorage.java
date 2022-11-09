package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.Item;

import java.util.List;

public interface ItemStorage {

    Item create(Item item);

    Item update(Item item);

    Item get(int userId);

    List<Item> getAll();

    List<Item> getAllByOwnerId(Integer ownerId);

    List<Item> getAllWantedItem(String text);
}
