package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

/**
* TODO Sprint add-item-requests.
*/
@Data
@Builder
@AllArgsConstructor
public class ItemRequest {
    private int id;
    private String description;
    private User requester;
    private LocalDateTime created;
}
