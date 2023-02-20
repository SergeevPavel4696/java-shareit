package ru.practicum.shareit.request;

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
import ru.practicum.shareit.request.controller.ItemRequestController;
import ru.practicum.shareit.request.model.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequestDtoResponse;
import ru.practicum.shareit.request.model.ItemRequestDtoWithItems;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
@AutoConfigureMockMvc
public class ItemRequestControllerTest {
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ItemRequestService itemRequestService;
    private ItemRequestDto correctRequestDto;
    private ItemRequestDto requestDtoWithIncorrectDescription;
    private ItemRequestDtoResponse requestDtoResponse;
    private ItemRequestDtoWithItems requestDtoWithItems;
    private final ValidationException validationException = new ValidationException("Ошибка валидации.");
    private final IncorrectId notFoundException = new IncorrectId("Объект не найден.");

    @BeforeEach
    public void beforeEach() {
        correctRequestDto = ItemRequestDto.builder().id(1).description("Описание").build();
        requestDtoWithIncorrectDescription = new ItemRequestDto(1, "");
        requestDtoResponse = ItemRequestDtoResponse.builder().id(1).description("Описание").created(LocalDateTime.of(2024, 1, 1, 0, 0, 0)).build();
        requestDtoWithItems = new ItemRequestDtoWithItems(1, "Описание",
                LocalDateTime.of(2024, 1, 1, 0, 0, 0),
                new ArrayList<>()
        );
    }

    @Test
    public void validationTestSuccess() throws Exception {
        when(itemRequestService.create(any(), anyInt())).thenReturn(requestDtoResponse);
        mockMvc.perform(post("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(correctRequestDto))
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.id", is(requestDtoResponse.getId()), Integer.class))
                .andExpect(jsonPath("$.description", is(requestDtoResponse.getDescription())));
    }

    @Test
    public void validationTestFail() throws Exception {
        when(itemRequestService.create(any(), anyInt())).thenThrow(validationException);
        mockMvc.perform(post("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDtoWithIncorrectDescription))
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void addItemRequestWithoutHeaderTestFail() throws Exception {
        mockMvc.perform(post("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(correctRequestDto)))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    public void getAllRequestsByUserIdTestSuccess() throws Exception {
        when(itemRequestService.getAllByRequesterId(anyInt())).thenReturn(List.of(requestDtoWithItems));
        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    public void getAllRequestsByUserIdNotFoundTestFail() throws Exception {
        when(itemRequestService.getAllByRequesterId(anyInt())).thenThrow(notFoundException);
        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 999))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.Error", is("Объект не найден.")));
    }

    @Test
    public void getAllRequestsByUserIdTestWithoutHeaderTestFail() throws Exception {
        mockMvc.perform(get("/requests"))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    public void getAllRequestsTestSuccess() throws Exception {
        when(itemRequestService.getAllByOtherRequester(anyInt(), anyInt(), anyInt())).thenReturn(List.of(requestDtoWithItems));
        mockMvc.perform(get("/requests/all?from=0&size=10")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    public void getAllRequestsNotFoundTestFail() throws Exception {
        when(itemRequestService.getAllByOtherRequester(anyInt(), anyInt(), anyInt())).thenThrow(notFoundException);
        mockMvc.perform(get("/requests/all?from=0&size=10")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.Error", is("Объект не найден.")));
    }

    @Test
    public void getAllRequestsWithoutHeaderTestFail() throws Exception {
        mockMvc.perform(get("/requests/all?from=0&size=10"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void getRequestByIdTestSuccess() throws Exception {
        when(itemRequestService.get(anyInt(), anyInt())).thenReturn(requestDtoWithItems);
        mockMvc.perform(get("/requests/1")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.id", is(requestDtoResponse.getId()), Integer.class))
                .andExpect(jsonPath("$.description", is(requestDtoResponse.getDescription())))
                .andExpect(jsonPath("$.items", hasSize(0)));
    }

    @Test
    public void getRequestByIdNotFoundTestFail() throws Exception {
        when(itemRequestService.get(anyInt(), anyInt())).thenThrow(notFoundException);
        mockMvc.perform(get("/requests/10")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.Error", is("Объект не найден.")));

        mockMvc.perform(get("/requests/1")
                        .header("X-Sharer-User-Id", 20))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.Error", is("Объект не найден.")));
    }

    @Test
    public void getRequestByIdWithoutHeaderTestFail() throws Exception {
        mockMvc.perform(get("/requests/1"))
                .andExpect(status().is4xxClientError());
    }
}
