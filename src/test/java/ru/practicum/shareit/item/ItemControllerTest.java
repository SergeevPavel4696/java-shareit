package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exceptions.IncorrectId;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.CommentDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
@AutoConfigureMockMvc
public class ItemControllerTest {
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ItemService itemService;
    private Item correctItem;
    private ItemDto correctItemDto;
    private Item itemWithoutHeader;
    private Item itemWithoutExistingUser;
    private Item itemWithIncorrectName;
    private Item itemWithIncorrectDescription;
    private Item itemWithoutAvailableField;
    private CommentDto commentDto;
    private final ValidationException validationException = new ValidationException("Ошибка валидации.");
    private final IncorrectId notFoundException = new IncorrectId("Объект не найден.");

    @BeforeEach
    public void beforeEach() {
        User owner = new User(1, "Владелец", "owner.mail.ya");
        User author = new User(1, "Комментатор", "author.mail.ya");
        correctItem = new Item(1, "correctItem", "correctItem", true, owner, 1);
        correctItemDto = ItemMapper.convert(correctItem);
        itemWithoutHeader = new Item(2, "itemWithoutHeader", "itemWithoutHeader", true, owner, 1);
        itemWithoutExistingUser = new Item(3, "itemWithoutExistingUser", "itemWithoutExistingUser", true, null, 1);
        itemWithIncorrectName = new Item(4, "", "itemWithIncorrectName", true, owner, 1);
        itemWithIncorrectDescription = new Item(5, "itemWithIncorrectDescription", "", true, owner, 1);
        itemWithoutAvailableField = new Item(6, "itemWithoutAvailableField", "itemWithoutAvailableField", null, owner, 1);
        commentDto = new CommentDto(1, "Комментарий", 1, author.getName());
    }

    @Test
    public void validationTestFail() throws Exception {
        when(itemService.create(any(), anyInt())).thenThrow(validationException);
        mockMvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemWithIncorrectName))
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.Error", is("Ошибка валидации.")));
        mockMvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemWithIncorrectDescription))
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.Error", is("Ошибка валидации.")));
        mockMvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemWithoutAvailableField))
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.Error", is("Ошибка валидации.")));
    }

    @Test
    public void createNoHeaderTest() throws Exception {
        mockMvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemWithoutHeader)))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    public void createUserNotFoundTest() throws Exception {
        when(itemService.create(any(), anyInt())).thenThrow(notFoundException);
        mockMvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemWithoutExistingUser))
                        .header("X-Sharer-User-Id", 999))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.Error", is("Объект не найден.")));
    }

    @Test
    public void updateTest() throws Exception {
        when(itemService.update(anyInt(), anyInt(), any())).thenReturn(correctItemDto);
        mockMvc.perform(patch("/items/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(correctItem))
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.id", is(correctItem.getId()), Integer.class))
                .andExpect(jsonPath("$.name", is(correctItem.getName())))
                .andExpect(jsonPath("$.description", is(correctItem.getDescription())))
                .andExpect(jsonPath("$.available", is(correctItem.getAvailable())))
                .andExpect(jsonPath("$.requestId", is(correctItem.getRequestId()), Integer.class));
    }

    @Test
    public void updateTestValidation() throws Exception {
        when(itemService.update(anyInt(), anyInt(), any())).thenThrow(validationException);
        mockMvc.perform(patch("/items/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemWithIncorrectName))
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.Error", is("Ошибка валидации.")));
    }

    @Test
    public void updateNotFoundTest() throws Exception {
        when(itemService.update(anyInt(), anyInt(), any())).thenThrow(notFoundException);
        mockMvc.perform(patch("/items/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(correctItem))
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.Error", is("Объект не найден.")));
    }

    @Test
    public void getAllByUserIdNotFoundTest() throws Exception {
        when(itemService.getAllByOwnerId(anyInt(), anyInt(), anyInt())).thenThrow(notFoundException);
        mockMvc.perform(get("/items?from=0&size=10")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.Error", is("Объект не найден.")));
    }

    @Test
    public void getAllByTextTest() throws Exception {
        when(itemService.searchItemsByText(anyString(), anyInt(), anyInt())).thenReturn(List.of(correctItemDto));
        mockMvc.perform(get("/items/search?text=предмет&from=0&size=10")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    public void addCommentTest() throws Exception {
        when(itemService.addComment(any(), anyInt(), anyInt())).thenReturn(commentDto);
        mockMvc.perform(post("/items/1/comment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDto))
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.id", is(commentDto.getId()), Integer.class))
                .andExpect(jsonPath("$.text", is(commentDto.getText())))
                .andExpect(jsonPath("$.authorName", is(commentDto.getAuthorName())))
                .andExpect(jsonPath("$.itemId", is(commentDto.getItemId()), Integer.class));
    }

    @Test
    public void addCommentValidationTest() throws Exception {
        when(itemService.addComment(any(), anyInt(), anyInt())).thenThrow(validationException);
        mockMvc.perform(post("/items/1/comment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDto))
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.Error", is("Ошибка валидации.")));
    }
}
