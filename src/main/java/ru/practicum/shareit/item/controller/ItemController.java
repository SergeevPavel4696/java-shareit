package ru.practicum.shareit.item.controller;

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
import ru.practicum.shareit.item.model.CommentDto;
import ru.practicum.shareit.item.model.ItemDto;
import ru.practicum.shareit.item.model.ItemWithBooking;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@RestController
@RequestMapping(path = "/items")
public class ItemController {

    ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public ItemDto createItem(
            @RequestHeader(value = "X-Sharer-User-Id", required = false) Integer ownerId,
            @RequestBody ItemDto itemDto) {
        return itemService.create(itemDto, ownerId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(
            @RequestHeader(value = "X-Sharer-User-Id", required = false) Integer ownerId,
            @PathVariable("itemId") int itemId,
            @RequestBody ItemDto itemDto) {
        return itemService.update(itemId, ownerId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ItemWithBooking getItem(@PathVariable("itemId") int itemId, @RequestHeader("X-Sharer-User-Id") Integer ownerId) {
        return itemService.get(itemId, ownerId);
    }

    @GetMapping
    public List<ItemWithBooking> getAllItems(@RequestHeader(value = "X-Sharer-User-Id", required = false) Integer ownerId) {
        return itemService.getAllByOwnerId(ownerId);
    }

    @GetMapping("/search")
    public List<ItemDto> getWantedItems(@RequestParam(name = "text") String text) {
        return itemService.searchItemsByText(text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(
            @RequestBody CommentDto comment,
            @PathVariable Integer itemId,
            @RequestHeader("X-Sharer-User-Id") Integer authorId) {
        return itemService.addComment(comment, itemId, authorId);
    }
}
