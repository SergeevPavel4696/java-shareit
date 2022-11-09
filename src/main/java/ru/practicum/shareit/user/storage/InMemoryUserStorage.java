package ru.practicum.shareit.user.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component("userStorage")
@RequiredArgsConstructor
public class InMemoryUserStorage implements UserStorage {

    private final Map<Integer, User> users = new HashMap<>();
    private int id = 0;

    @Override
    public User create(User user) {
        user.setId(++id);
        users.put(user.getId(), user);
        return get(user.getId());
    }

    @Override
    public User update(User user) {
        users.put(user.getId(), user);
        return get(user.getId());
    }

    @Override
    public User delete(int userId) {
        return users.remove(userId);
    }

    @Override
    public User get(int userId) {
        return users.get(userId);
    }

    @Override
    public List<User> getAll() {
        return new ArrayList<>(users.values());
    }
}
