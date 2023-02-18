//package ru.practicum.shareit.user;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mockito;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//import ru.practicum.shareit.exceptions.EntityAlreadyExistsException;
//import ru.practicum.shareit.exceptions.EntityNotFoundException;
//import ru.practicum.shareit.exceptions.IllegalEntityAccessException;
//import ru.practicum.shareit.exceptions.ValidationException;
//import ru.practicum.shareit.user.model.User;
//import ru.practicum.shareit.user.service.UserService;
//
//import java.util.List;
//
//import static org.hamcrest.Matchers.hasSize;
//import static org.hamcrest.Matchers.is;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.when;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@WebMvcTest(UserController.class)
//@AutoConfigureMockMvc
//public class UserControllerTest {
//    @Autowired
//    ObjectMapper objectMapper;
//    @Autowired
//    private MockMvc mockMvc;
//    @MockBean
//    private UserService userService;
//    private User correctUser;
//    private User userWithIncorrectName;
//    private User userWithIncorrectEmail;
//    private User userWithDuplicateEmail;
//    private final ValidationException validationException = new ValidationException("Валидация.");
//    private final EntityAlreadyExistsException entityAlreadyExistsException = new EntityAlreadyExistsException("Уже существует.");
//    private final EntityNotFoundException notFoundException = new EntityNotFoundException("Не найдено.");
//    private final IllegalEntityAccessException illegalEntityAccessException = new IllegalEntityAccessException("Нет доступа.");
//
//    @BeforeEach
//    public void beforeEach() {
//        correctUser = new User(1L, "e5k4p3", "e5k4p3@gmail.com");
//        userWithIncorrectName = new User(2L, "", "e5k4p3@gmail.com");
//        userWithIncorrectEmail = new User(3L, "e5k4p3", "@gmail.com");
//        userWithDuplicateEmail = new User(4L, "e5k4p3", "e5k4p3@gmail.com");
//    }
//
//    @Test
//    public void validationTestSuccess() throws Exception {
//        when(userService.addUser(any())).thenReturn(correctUser);
//
//        mockMvc.perform(post("/users")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(correctUser)))
//                .andExpect(status().is2xxSuccessful())
//                .andExpect(jsonPath("$.id", is(correctUser.getId()), Long.class))
//                .andExpect(jsonPath("$.name", is(correctUser.getName())))
//                .andExpect(jsonPath("$.email", is(correctUser.getEmail())));
//    }
//
//    @Test
//    public void validationTestFail() throws Exception {
//        when(userService.addUser(any())).thenThrow(validationException);
//
//        mockMvc.perform(post("/users")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(userWithIncorrectName)))
//                .andExpect(status().is4xxClientError())
//                .andExpect(jsonPath("$.error", is("Ошибка валидации.")));
//
//        mockMvc.perform(post("/users")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(userWithIncorrectEmail)))
//                .andExpect(status().is4xxClientError())
//                .andExpect(jsonPath("$.error", is("Ошибка валидации.")));
//    }
//
//    @Test
//    public void addUserDuplicateEmailFail() throws Exception {
//        when(userService.addUser(any())).thenThrow(entityAlreadyExistsException);
//
//        mockMvc.perform(post("/users")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(userWithDuplicateEmail)))
//                .andExpect(status().is4xxClientError())
//                .andExpect(jsonPath("$.error", is("Такой объект уже существует.")));
//    }
//
//    @Test
//    public void updateTestSuccess() throws Exception {
//        when(userService.updateUser(Mockito.anyLong(), any())).thenReturn(correctUser);
//
//        mockMvc.perform(patch("/users/1")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(correctUser)))
//                .andExpect(status().is2xxSuccessful())
//                .andExpect(jsonPath("$.id", is(correctUser.getId()), Long.class))
//                .andExpect(jsonPath("$.name", is(correctUser.getName())))
//                .andExpect(jsonPath("$.email", is(correctUser.getEmail())));
//    }
//
//    @Test
//    public void updateTestValidationFail() throws Exception {
//        when(userService.updateUser(Mockito.anyLong(), any())).thenThrow(validationException);
//
//        mockMvc.perform(patch("/users/1")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(correctUser)))
//                .andExpect(status().is4xxClientError())
//                .andExpect(jsonPath("$.error", is("Ошибка валидации.")));
//    }
//
//    @Test
//    public void updateTestDuplicateEmailFail() throws Exception {
//        when(userService.updateUser(Mockito.anyLong(), any())).thenThrow(entityAlreadyExistsException);
//
//        mockMvc.perform(patch("/users/1")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(correctUser)))
//                .andExpect(status().is4xxClientError())
//                .andExpect(jsonPath("$.error", is("Такой объект уже существует.")));
//    }
//
//    @Test
//    public void updateTestNotFoundFail() throws Exception {
//        when(userService.updateUser(Mockito.anyLong(), any())).thenThrow(notFoundException);
//
//        mockMvc.perform(patch("/users/1")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(correctUser)))
//                .andExpect(status().is4xxClientError())
//                .andExpect(jsonPath("$.error", is("Объект не найден.")));
//    }
//
//    @Test
//    public void updateTestIllegalAccessFail() throws Exception {
//        when(userService.updateUser(Mockito.anyLong(), any())).thenThrow(illegalEntityAccessException);
//
//        mockMvc.perform(patch("/users/1")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(correctUser)))
//                .andExpect(status().is4xxClientError())
//                .andExpect(jsonPath("$.error", is("Отсутствие прав доступа к объекту.")));
//    }
//
//    @Test
//    public void deleteTestSuccess() throws Exception {
//        Mockito.doNothing().when(userService).deleteUser(Mockito.anyLong());
//
//        mockMvc.perform(delete("/users/1"))
//                .andExpect(status().is2xxSuccessful());
//    }
//
//    @Test
//    public void deleteTestNotFoundFail() throws Exception {
//        Mockito.doThrow(notFoundException).when(userService).deleteUser(Mockito.anyLong());
//
//        mockMvc.perform(delete("/users/1"))
//                .andExpect(status().is4xxClientError())
//                .andExpect(jsonPath("$.error", is("Объект не найден.")));
//    }
//
//    @Test
//    public void getByIdSuccess() throws Exception {
//        when(userService.getUserById(Mockito.anyLong())).thenReturn(correctUser);
//
//        mockMvc.perform(get("/users/1"))
//                .andExpect(status().is2xxSuccessful())
//                .andExpect(jsonPath("$.id", is(correctUser.getId()), Long.class));
//    }
//
//    @Test
//    public void getByIdNotFoundFail() throws Exception {
//        when(userService.getUserById(Mockito.anyLong())).thenThrow(notFoundException);
//
//        mockMvc.perform(get("/users/1"))
//                .andExpect(status().is4xxClientError())
//                .andExpect(jsonPath("$.error", is("Объект не найден.")));
//    }
//
//    @Test
//    public void getAllSuccess() throws Exception {
//        when(userService.getAllUsers()).thenReturn(List.of(correctUser));
//
//        mockMvc.perform(get("/users"))
//                .andExpect(status().is2xxSuccessful())
//                .andExpect(jsonPath("$", hasSize(1)));
//    }
//}