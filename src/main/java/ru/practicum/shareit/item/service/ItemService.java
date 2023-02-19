package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingEntry;
import ru.practicum.shareit.booking.repository.BookingJpaRepository;
import ru.practicum.shareit.exceptions.Duplicate;
import ru.practicum.shareit.exceptions.IncorrectId;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.CommentDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemDto;
import ru.practicum.shareit.item.model.ItemDtoWithBooking;
import ru.practicum.shareit.item.repository.CommentJpaRepository;
import ru.practicum.shareit.item.repository.ItemJpaRepository;
import ru.practicum.shareit.item.validator.CommentValidator;
import ru.practicum.shareit.item.validator.ItemIdValidator;
import ru.practicum.shareit.item.validator.ItemValidator;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.validator.UserIdValidator;
import ru.practicum.shareit.util.PageableMaker;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.booking.model.BookingStatus.APPROVED;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final UserService userService;
    private final ItemJpaRepository itemJpaRepository;
    private final BookingJpaRepository bookingJpaRepository;
    private final CommentJpaRepository commentJpaRepository;

    public ItemDto create(ItemDto itemDto, Integer ownerId) {
        ItemValidator.validate(itemDto, ownerId);
        UserIdValidator.validate(userService.get(ownerId));
        User owner = userService.getUser(ownerId);
        Item item = ItemMapper.convert(itemDto, owner);
        ItemValidator.validate(item);
        try {
            return ItemMapper.convert(itemJpaRepository.save(item));
        } catch (DataIntegrityViolationException e) {
            throw new Duplicate("Пользователь с указанными данными уже существует.");
        }
    }

    public ItemDto update(int itemId, Integer ownerId, ItemDto itemDto) {
        ItemIdValidator.validate(getItem(itemId));
        ItemValidator.validate(itemDto, ownerId);
        UserIdValidator.validate(userService.get(ownerId));
        Item item = itemJpaRepository.findById(itemId);
        if (!item.getOwner().getId().equals(ownerId)) {
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
        try {
            item = itemJpaRepository.save(item);
            return ItemMapper.convert(item);
        } catch (DataIntegrityViolationException e) {
            throw new Duplicate("Пользователь с указанными данными уже существует.");
        }
    }

    public ItemDtoWithBooking get(int itemId, int ownerId) {
        ItemIdValidator.validate(getItem(itemId));
        Item item = itemJpaRepository.findById(itemId);
        ItemDtoWithBooking itemWithBooking = ItemMapper.convert(item, getLastBookingForItem(itemId), getNextBookingForItem(itemId), getCommentsByItemId(itemId));
        if (item.getOwner().getId() != ownerId) {
            itemWithBooking.setLastBooking(null);
            itemWithBooking.setNextBooking(null);
        }
        return itemWithBooking;
    }

    public Item getItem(int itemId) {
        Item item = itemJpaRepository.findById(itemId);
        ItemIdValidator.validate(item);
        return item;
    }

    public List<ItemDtoWithBooking> getAllByOwnerId(Integer ownerId, Integer from, Integer size) {
        UserIdValidator.validate(userService.get(ownerId));
        User owner = userService.getUser(ownerId);
        List<Item> items = itemJpaRepository.findAllByOwner(owner, PageableMaker.makePage(from, size));
        List<ItemDtoWithBooking> itemDtos = new ArrayList<>();
        for (Item item : items) {
            BookingEntry lastBooking = getLastBookingForItem(item.getId());
            BookingEntry nextBooking = getNextBookingForItem(item.getId());
            ItemDtoWithBooking itemWithBooking = ItemMapper.convert(item, lastBooking, nextBooking, getCommentsByItemId(item.getId()));
            itemDtos.add(itemWithBooking);
        }
        return itemDtos;
    }

    private BookingEntry getNextBookingForItem(int itemId) {
        List<Booking> nextBookings = bookingJpaRepository.findAll().stream()
                .filter(booking -> booking.getStart().isAfter(LocalDateTime.now()) && booking.getItem().getId() == itemId)
                .sorted(Comparator.comparing(Booking::getStart))
                .collect(Collectors.toList());
        if (!nextBookings.isEmpty()) {
            return BookingMapper.convert(nextBookings.get(0));
        } else {
            return null;
        }
    }

    private BookingEntry getLastBookingForItem(int itemId) {
        List<Booking> lastBookings = bookingJpaRepository.findAll().stream()
                .filter(booking -> booking.getEnd().isBefore(LocalDateTime.now()) && booking.getItem().getId() == itemId)
                .sorted(Comparator.comparing(Booking::getStart).reversed())
                .collect(Collectors.toList());
        if (!lastBookings.isEmpty()) {
            return BookingMapper.convert(lastBookings.get(0));
        } else {
            return null;
        }
    }

    public List<ItemDto> searchItemsByText(String text, Integer from, Integer size) {
        if (text != null && !text.isEmpty()) {
            return itemJpaRepository.findItemsContainingText(text, PageableMaker.makePage(from, size)).stream()
                    .map(ItemMapper::convert).collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    public CommentDto addComment(CommentDto commentDto, int itemId, int authorId) {
        if (bookingJpaRepository.findAll().stream().noneMatch(booking -> booking.getBooker().getId() == authorId
                && booking.getItem().getId() == itemId && booking.getStatus() == APPROVED)) {
            throw new ValidationException("Вы не можете комментировать не одобренную к бронированию вещь.");
        }
        if (bookingJpaRepository.findAll().stream().noneMatch(booking -> booking.getBooker().getId() == authorId
                && booking.getItem().getId() == itemId && booking.getStart().isBefore(LocalDateTime.now()))) {
            throw new ValidationException("Вы не можете комментировать вещь, которой ещё не пользовались.");
        }
        CommentValidator.validate(commentDto);
        Comment comment = CommentMapper.convert(commentDto, getItem(itemId), userService.getUser(authorId));
        CommentValidator.validate(comment);
        commentJpaRepository.save(comment);
        return CommentMapper.convert(comment);
    }

    public List<CommentDto> getCommentsByItemId(int itemId) {
        return commentJpaRepository.findAll().stream().filter(comment -> comment.getItem().getId() == itemId)
                .map(CommentMapper::convert).collect(Collectors.toList());
    }

    public List<ItemDto> getAllByItemRequestId(Integer itemRequestId) {
        return itemJpaRepository.findAllByRequestId(itemRequestId).stream().map(ItemMapper::convert).collect(Collectors.toList());
    }
}
