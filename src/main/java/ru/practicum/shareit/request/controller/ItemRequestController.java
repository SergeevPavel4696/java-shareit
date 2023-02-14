package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.request.model.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequestDtoResponse;
import ru.practicum.shareit.request.model.ItemRequestDtoWithItems;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDtoResponse createItemRequest(
            @RequestHeader(value = "X-Sharer-User-Id", required = false) Integer requesterId,
            @RequestBody ItemRequestDto itemRequestDto) {
        return itemRequestService.create(itemRequestDto, requesterId);
    }

    @GetMapping
    public List<ItemRequestDtoWithItems> getMyItemRequests(
            @RequestHeader(value = "X-Sharer-User-Id", required = false) Integer requesterId) {
        return itemRequestService.getAllByRequesterId(requesterId);
    }

    @GetMapping("/all")
    public List<ItemRequestDtoWithItems> getAllRequests(
            @RequestHeader("X-Sharer-User-Id") Integer myId,
            @RequestParam(name = "from", defaultValue = "0") Integer from,
            @RequestParam(name = "size", defaultValue = "10") Integer size) {
        return itemRequestService.getAllByOtherRequester(myId, from, size);
    }

    @GetMapping("/{itemRequestId}")
    public ItemRequestDtoWithItems getRequestById(
            @PathVariable Integer itemRequestId,
            @RequestHeader("X-Sharer-User-Id") Integer userId) {
        return itemRequestService.get(itemRequestId, userId);
    }
}
