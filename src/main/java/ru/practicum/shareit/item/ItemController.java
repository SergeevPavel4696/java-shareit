package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/items")
public class ItemController {

    ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public Item createItem(@RequestHeader(value = "X-Sharer-User-Id", required = false) Integer ownerId,
                           @RequestBody ItemDto itemDto) {
        return itemService.create(itemDto, ownerId);
    }

    @PatchMapping("/{itemId}")
    public Item updateItem(@RequestHeader(value = "X-Sharer-User-Id", required = false) Integer ownerId,
                           @PathVariable("itemId") int itemId, @RequestBody ItemDto itemDto) {
        return itemService.update(itemId, ownerId, itemDto);
    }

    @GetMapping("/{itemId}")
    public Item getItem(@PathVariable("itemId") int itemId) {
        return itemService.get(itemId);
    }

    @GetMapping
    public List<Item> getAllItems(@RequestHeader(value = "X-Sharer-User-Id", required = false) Integer ownerId) {
        return itemService.getAllByOwnerId(ownerId);
    }

    @GetMapping("/search")
    public List<Item> getWantedItems(@RequestParam(name = "text") String text) {
        return itemService.searchItemsByText(text);
    }
}
