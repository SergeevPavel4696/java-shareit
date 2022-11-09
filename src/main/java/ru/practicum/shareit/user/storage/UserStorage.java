package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.User;

import java.util.List;

public interface UserStorage {

    User create(User user);

    User update(User user);

    User delete(int userId);

    User get(int userId);

    List<User> getAll();
}
