package ru.practicum.shareit.item.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exceptions.IncorrectId;
import ru.practicum.shareit.item.Item;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class DBItemStorage implements ItemStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Item create(Item item) {
        String query = "INSERT INTO items (name, description, available, owner_id) VALUES (?, ?, ?, ?);";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        final int isAvailable;
        if (item.getAvailable()) {
            isAvailable = 1;
        } else {
            isAvailable = 0;
        }
        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(query, new String[]{"id"});
            statement.setString(1, item.getName());
            statement.setString(2, item.getDescription());
            statement.setInt(3, isAvailable);
            statement.setInt(4, item.getOwnerId());
            return statement;
        }, keyHolder);
        item.setId(Objects.requireNonNull(keyHolder.getKey()).intValue());
        return get(item.getId());
    }

    @Override
    public Item update(Item item) {
        String query = "UPDATE items SET name = ?, description = ?, available = ? WHERE id = ?;";
        jdbcTemplate.update(query, item.getName(), item.getDescription(), item.getAvailable(), item.getId());
        return get(item.getId());
    }

    @Override
    public Item get(int itemId) {
        String query = "SELECT * FROM items WHERE id = ?;";
        return jdbcTemplate.query(query, (rs, rowNum) -> makeItem(rs), itemId)
                .stream()
                .findAny()
                .orElseThrow(() -> new IncorrectId("Вещь по указанному id не существует."));
    }

    @Override
    public List<Item> getAll() {
        String query = "SELECT * FROM items;";
        return jdbcTemplate.query(query, (rs, rowNum) -> makeItem(rs));
    }

    @Override
    public List<Item> getAllByOwnerId(Integer ownerId) {
        String query = "SELECT * FROM items WHERE owner_id = ?;";
        return jdbcTemplate.query(query, (rs, rowNum) -> makeItem(rs), ownerId);
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

    private Item makeItem(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String name = rs.getString("name");
        String description = rs.getString("description");
        boolean isAvailable = rs.getInt("available") == 1;
        int ownerId = rs.getInt("owner_id");
        return new Item(id, name, description, isAvailable, ownerId);
    }
}
