package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingDto;
import ru.practicum.shareit.booking.repository.BookingJpaRepository;
import ru.practicum.shareit.exceptions.IncorrectId;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.CommentDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemDto;
import ru.practicum.shareit.item.model.ItemWithBooking;
import ru.practicum.shareit.item.repository.CommentJpaRepository;
import ru.practicum.shareit.item.repository.ItemJpaRepository;
import ru.practicum.shareit.item.validator.CommentValidator;
import ru.practicum.shareit.item.validator.ItemIdValidator;
import ru.practicum.shareit.item.validator.ItemValidator;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.validator.UserIdValidator;

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
        User owner = userService.getUser(ownerId);
        Item item = ItemMapper.convert(itemDto, owner);
        ItemValidator.validate(item);
        UserIdValidator.validate(userService.getUsersId(), ownerId);
        item = itemJpaRepository.save(item);
        itemDto = ItemMapper.convert(item);
        return itemDto;
    }

    public ItemDto update(int itemId, Integer ownerId, ItemDto itemDto) {
        ItemIdValidator.validate(getItemsId(), itemId);
        ItemValidator.validate(itemDto, ownerId);
        UserIdValidator.validate(userService.getUsersId(), ownerId);
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
        item = itemJpaRepository.save(item);
        itemDto = ItemMapper.convert(item);
        return itemDto;
    }

    public ItemWithBooking get(int itemId, int ownerId) {
        ItemIdValidator.validate(getItemsId(), itemId);
        Item item = itemJpaRepository.findById(itemId);
        ItemWithBooking itemWithBooking = ItemMapper.convert(item, getLastBookingForItem(itemId), getNextBookingForItem(itemId), getCommentsByItemId(itemId));
        if (item.getOwner().getId() != ownerId) {
            itemWithBooking.setLastBooking(null);
            itemWithBooking.setNextBooking(null);
        }
        return itemWithBooking;
    }

    public Item getItem(int itemId) {
        ItemIdValidator.validate(getItemsId(), itemId);
        return itemJpaRepository.findById(itemId);
    }

    public List<ItemWithBooking> getAllByOwnerId(Integer ownerId) {
        UserIdValidator.validate(userService.getUsersId(), ownerId);
        User owner = userService.getUser(ownerId);
        List<Item> items = itemJpaRepository.findAllByOwner(owner);
        List<ItemWithBooking> itemDtos = new ArrayList<>();
        for (Item item : items) {
            BookingDto lastBooking = getLastBookingForItem(item.getId());
            BookingDto nextBooking = getNextBookingForItem(item.getId());
            ItemWithBooking itemWithBooking = ItemMapper.convert(item, lastBooking, nextBooking, getCommentsByItemId(item.getId()));
            itemDtos.add(itemWithBooking);
        }
        return itemDtos;
    }

    private BookingDto getNextBookingForItem(int itemId) {
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

    private BookingDto getLastBookingForItem(int itemId) {
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

    public List<ItemDto> searchItemsByText(String text) {
        if (text != null && !text.isEmpty()) {
            return itemJpaRepository.findItemsContainingText(text).stream()
                    .map(ItemMapper::convert).collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    public Comment addComment(CommentDto commentDto, int itemId, int authorId) {
        if (bookingJpaRepository.findAll().stream().noneMatch(booking -> booking.getBooker().getId() == authorId
                && booking.getItem().getId() == itemId
                && booking.getStatus() == APPROVED)) {
            throw new ValidationException("Вы не можете комментировать не одобренную к бронированию вещь.");
        }
        if (bookingJpaRepository.findAll().stream().noneMatch(booking -> booking.getBooker().getId() == authorId
                && booking.getItem().getId() == itemId
                && booking.getStart().isBefore(LocalDateTime.now()))) {
            throw new ValidationException("Вы не можете комментировать вещь, которой ещё не пользовались.");
        }
        CommentValidator.validate(commentDto, authorId);
        User booker = userService.getUser(authorId);
        Item item = getItem(itemId);
        Comment comment = CommentMapper.convert(commentDto, item, booker);
        CommentValidator.validate(comment);
        commentJpaRepository.save(comment);
        return comment;
    }

    public List<Integer> getItemsId() {
        return itemJpaRepository.findAll().stream().map(Item::getId).collect(Collectors.toList());
    }

    public List<Comment> getCommentsByItemId(int itemId) {
        return commentJpaRepository.findAll().stream()
                .filter(comment -> comment.getItem().getId() == itemId).collect(Collectors.toList());
    }
}
