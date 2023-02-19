package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exceptions.Duplicate;
import ru.practicum.shareit.exceptions.IncorrectId;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.user.controller.UserController;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc
public class UserControllerTest {
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UserService userService;
    private User correctUser;
    private UserDto correctUserDto;
    private User userWithIncorrectName;
    private User userWithIncorrectEmail;
    private User userWithDuplicateEmail;
    private final ValidationException validationException = new ValidationException("Ошибка валидации.");
    private final Duplicate entityAlreadyExistsException = new Duplicate("Объект уже существует.");
    private final IncorrectId notFoundException = new IncorrectId("Объект не найден.");

    @BeforeEach
    public void beforeEach() {
        correctUser = new User(1, "correctUser", "correctUser@mail.ya");
        correctUserDto = UserMapper.convert(correctUser);
        userWithIncorrectName = new User(2, "", "userWithIncorrectName@mail.ya");
        userWithIncorrectEmail = new User(3, "userWithIncorrectEmail", "@mail.ya");
        userWithDuplicateEmail = new User(4, "userWithDuplicateEmail", "correctUser@mail.ya");
    }

    @Test
    public void validationTest() throws Exception {
        when(userService.create(any())).thenReturn(correctUserDto);
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(correctUser)))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.id", is(correctUser.getId()), Integer.class))
                .andExpect(jsonPath("$.name", is(correctUser.getName())))
                .andExpect(jsonPath("$.email", is(correctUser.getEmail())));
    }

    @Test
    public void validationTestFail() throws Exception {
        when(userService.create(any())).thenThrow(validationException);
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userWithIncorrectName)))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.Error", is("Ошибка валидации.")));
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userWithIncorrectEmail)))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.Error", is("Ошибка валидации.")));
    }

    @Test
    public void createDuplicateEmailTest() throws Exception {
        when(userService.create(any())).thenThrow(entityAlreadyExistsException);
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userWithDuplicateEmail)))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.Error", is("Объект уже существует.")));
    }

    @Test
    public void updateTest() throws Exception {
        when(userService.update(anyInt(), any())).thenReturn(correctUserDto);
        mockMvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(correctUser)))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.id", is(correctUser.getId()), Integer.class))
                .andExpect(jsonPath("$.name", is(correctUser.getName())))
                .andExpect(jsonPath("$.email", is(correctUser.getEmail())));
    }

    @Test
    public void updateValidationTest() throws Exception {
        when(userService.update(anyInt(), any())).thenThrow(validationException);
        mockMvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(correctUser)))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.Error", is("Ошибка валидации.")));
    }

    @Test
    public void updateDuplicateEmailTest() throws Exception {
        when(userService.update(anyInt(), any())).thenThrow(entityAlreadyExistsException);
        mockMvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(correctUser)))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.Error", is("Объект уже существует.")));
    }

    @Test
    public void updateNotFoundTest() throws Exception {
        when(userService.update(anyInt(), any())).thenThrow(notFoundException);
        mockMvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(correctUser)))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.Error", is("Объект не найден.")));
    }

    @Test
    public void deleteTest() throws Exception {
        when(userService.delete(anyInt())).thenReturn(correctUserDto);
        mockMvc.perform(delete("/users/1"))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    public void deleteNotFoundTest() throws Exception {
        Mockito.doThrow(notFoundException).when(userService).delete(anyInt());
        mockMvc.perform(delete("/users/1"))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.Error", is("Объект не найден.")));
    }

    @Test
    public void getTest() throws Exception {
        when(userService.get(anyInt())).thenReturn(correctUserDto);
        mockMvc.perform(get("/users/1"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.id", is(correctUser.getId()), Integer.class));
    }

    @Test
    public void getNotFoundTest() throws Exception {
        when(userService.get(anyInt())).thenThrow(notFoundException);
        mockMvc.perform(get("/users/1"))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.Error", is("Объект не найден.")));
    }

    @Test
    public void getAllTest() throws Exception {
        when(userService.getAll()).thenReturn(List.of(correctUserDto));
        mockMvc.perform(get("/users"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$", hasSize(1)));
    }
}
