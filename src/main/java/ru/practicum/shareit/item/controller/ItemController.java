package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
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
import ru.practicum.shareit.item.model.ItemDtoWithBooking;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/items")
public class ItemController {

    private final ItemService itemService;

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
    public ItemDtoWithBooking getItem(
            @RequestHeader("X-Sharer-User-Id") Integer ownerId,
            @PathVariable("itemId") int itemId) {
        return itemService.get(itemId, ownerId);
    }

    @GetMapping
    public List<ItemDtoWithBooking> getAllItems(
            @RequestHeader(value = "X-Sharer-User-Id", required = false) Integer ownerId,
            @RequestParam(name = "from", defaultValue = "0") Integer from,
            @RequestParam(name = "size", defaultValue = "10") Integer size) {
        return itemService.getAllByOwnerId(ownerId, from, size);
    }

    @GetMapping("/search")
    public List<ItemDto> getWantedItems(
            @RequestParam(name = "text") String text,
            @RequestParam(name = "from", defaultValue = "0") Integer from,
            @RequestParam(name = "size", defaultValue = "10") Integer size) {
        return itemService.searchItemsByText(text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(
            @RequestHeader("X-Sharer-User-Id") Integer authorId,
            @PathVariable Integer itemId,
            @RequestBody CommentDto comment) {
        return itemService.addComment(comment, itemId, authorId);
    }
}
