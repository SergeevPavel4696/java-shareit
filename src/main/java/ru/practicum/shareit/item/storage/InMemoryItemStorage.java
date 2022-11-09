package ru.practicum.shareit.item.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component("itemStorage")
@RequiredArgsConstructor
public class InMemoryItemStorage implements ItemStorage {

    private final Map<Integer, Item> items = new HashMap<>();
    private int id = 0;

    @Override
    public Item create(Item item) {
        item.setId(++id);
        items.put(item.getId(), item);
        return get(item.getId());
    }

    @Override
    public Item update(Item item) {
        items.put(item.getId(), item);
        return get(item.getId());
    }

    @Override
    public Item get(int itemId) {
        return items.get(itemId);
    }

    @Override
    public List<Item> getAll() {
        return new ArrayList<>(items.values());
    }

    @Override
    public List<Item> getAllByOwnerId(Integer ownerId) {
        List<Item> ownersItems = new ArrayList<>();
        for (Item item : items.values()) {
            if (item.getOwnerId().equals(ownerId)) {
                ownersItems.add(item);
            }
        }
        return ownersItems;
    }

    @Override
    public List<Item> getAllWantedItem(String text) {
        List<Item> allItems = getAll();
        List<Item> wantedItems = new ArrayList<>();
        for (Item item : allItems) {
            if ((item.getName().toLowerCase().contains(text.toLowerCase())
                    || item.getDescription().toLowerCase().contains(text.toLowerCase()))
                    && item.getAvailable()) {
                wantedItems.add(item);
            }
        }
        return wantedItems;
    }
}
