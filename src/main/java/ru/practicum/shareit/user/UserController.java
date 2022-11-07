package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {

    UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public User createUser(@RequestBody User user) {
        return userService.create(user);
    }

    @PatchMapping("/{userId}")
    public User updateUser(@PathVariable("userId") int userId, @RequestBody UserDto userDto) {
        return userService.update(userId, userDto);
    }

    @DeleteMapping("/{userId}")
    public User deleteUser(@PathVariable("userId") int userId) {
        return userService.delete(userId);
    }

    @GetMapping("/{userId}")
    public User getUser(@PathVariable("userId") int userId) {
        return userService.get(userId);
    }

    @GetMapping
    public List<User> getAllUser() {
        return userService.getAll();
    }
}
