package ru.practicum.shareit.user.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exceptions.IncorrectId;
import ru.practicum.shareit.user.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class DBUserStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public User create(User user) {
        String query = "INSERT INTO users (name, email) VALUES (?, ?);";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(query, new String[]{"id"});
            statement.setString(1, user.getName());
            statement.setString(2, user.getEmail());
            return statement;
        }, keyHolder);
        user.setId(Objects.requireNonNull(keyHolder.getKey()).intValue());
        return get(user.getId());
    }

    @Override
    public User update(User user) {
        String query = "UPDATE users SET name = ?, email = ? WHERE id = ?;";
        jdbcTemplate.update(query, user.getName(), user.getEmail(), user.getId());
        return get(user.getId());
    }

    @Override
    public User delete(int userId) {
        User user = get(userId);
        String query = "DELETE users WHERE id = ?;";
        jdbcTemplate.update(query, user.getId());
        return user;
    }

    @Override
    public User get(int userId) {
        String query = "SELECT * FROM users WHERE id = ?;";
        return jdbcTemplate.query(query, (rs, rowNum) -> makeUser(rs), userId)
                .stream()
                .findAny()
                .orElseThrow(() -> new IncorrectId("Пользователь по указанному id не существует."));
    }

    @Override
    public List<User> getAll() {
        String query = "SELECT * FROM users;";
        return jdbcTemplate.query(query, (rs, rowNum) -> makeUser(rs));
    }

    private User makeUser(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String name = rs.getString("name");
        String email = rs.getString("email");
        return new User(id, name, email);
    }
}
