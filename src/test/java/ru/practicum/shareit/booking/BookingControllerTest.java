package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.controller.BookingController;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exceptions.IncorrectBookingStatus;
import ru.practicum.shareit.exceptions.IncorrectId;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.booking.model.BookingStatus.APPROVED;
import static ru.practicum.shareit.booking.model.BookingStatus.WAITING;

@WebMvcTest(BookingController.class)
@AutoConfigureMockMvc
public class BookingControllerTest {
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private BookingService bookingService;
    private Booking booking;
    private BookingDto bookingDto;
    private BookingDto correctBooking;
    private BookingDto bookingWithStartIsNull;
    private BookingDto bookingWithEndIsNull;
    private BookingDto bookingWithStartInPast;
    private BookingDto bookingWithEndInPast;
    private BookingDto bookingWithStartAfterEnd;
    private BookingDto bookingWithoutExistingItem;
    private User owner;
    private User booker;
    private Item item;
    LocalDateTime start;
    LocalDateTime end;
    private final ValidationException validationException = new ValidationException("Ошибка валидации.");
    private final IncorrectId notFoundException = new IncorrectId("Объект не найден.");
    private final IllegalArgumentException illegalArgumentException = new IllegalArgumentException("Недопустимый аргумент.");
    private final IncorrectBookingStatus incorrectBookingStatus = new IncorrectBookingStatus("Неподдерживаемый статус бронирования.");

    @BeforeEach
    public void beforeEach() {
        owner = new User(1, "Владелец", "owner@mail.ya");
        booker = new User(1, "Арендатор", "booker@mail.ya");
        item = new Item(1, "Вещь", "Описание", true, owner, 1);
        start = LocalDateTime.of(2024, 1, 1, 0, 0, 0);
        end = LocalDateTime.of(2024, 1, 2, 1, 1, 1);
        booking = new Booking(1, start, end, item, booker, APPROVED);
        bookingDto = BookingMapper.convertToDto(booking);
        correctBooking = new BookingDto(1, start, end, item, null, WAITING);
        bookingWithStartIsNull = new BookingDto(2, null, end, item, booker, WAITING);
        bookingWithEndIsNull = new BookingDto(3, start, null, item, booker, WAITING);
        bookingWithStartInPast = new BookingDto(4,
                LocalDateTime.of(2022, 1, 1, 0, 0, 0), end, item, booker, WAITING);
        bookingWithEndInPast = new BookingDto(5, start,
                LocalDateTime.of(2022, 1, 1, 0, 0, 0), item, booker, WAITING);
        bookingWithStartAfterEnd = new BookingDto(6, end, start, item, booker, WAITING);
        bookingWithoutExistingItem = new BookingDto(7, start, end, null, booker, WAITING);
    }

    @Test
    public void validationTest() throws Exception {
        when(bookingService.create(any(), anyInt())).thenReturn(bookingDto);
        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(correctBooking))
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.id", is(booking.getId()), Integer.class))
                .andExpect(jsonPath("$.end", is(booking.getEnd().toString())))
                .andExpect(jsonPath("$.item.id", is(booking.getItem().getId())))
                .andExpect(jsonPath("$.booker.id", is(booking.getBooker().getId())))
                .andExpect(jsonPath("$.status", is(APPROVED.toString())));
    }

    @Test
    public void validationFailTest() throws Exception {
        when(bookingService.create(any(), anyInt())).thenThrow(validationException);
        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingWithStartIsNull))
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().is4xxClientError());
        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingWithEndIsNull))
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().is4xxClientError());
        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingWithStartInPast))
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().is4xxClientError());
        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingWithEndInPast))
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().is4xxClientError());
        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingWithStartAfterEnd))
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.Error", is("Ошибка валидации.")));
    }

    @Test
    public void createNotFoundTest() throws Exception {
        when(bookingService.create(any(), anyInt())).thenThrow(notFoundException);
        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(correctBooking))
                        .header("X-Sharer-User-Id", 999))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.Error", is("Объект не найден.")));
        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingWithoutExistingItem))
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.Error", is("Объект не найден.")));
    }

    @Test
    public void updateTest() throws Exception {
        booking.setStatus(APPROVED);
        when(bookingService.reactToBooking(anyInt(), anyBoolean(), anyInt())).thenReturn(bookingDto);
        mockMvc.perform(patch("/bookings/1?approved=true")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.id", is(booking.getId()), Integer.class))
                .andExpect(jsonPath("$.end", is(booking.getEnd().toString())))
                .andExpect(jsonPath("$.item.id", is(booking.getItem().getId())))
                .andExpect(jsonPath("$.booker.id", is(booking.getBooker().getId())))
                .andExpect(jsonPath("$.status", is(APPROVED.toString())));
    }

    @Test
    public void updateNotFoundTest() throws Exception {
        when(bookingService.reactToBooking(anyInt(), anyBoolean(), anyInt())).thenThrow(notFoundException);
        mockMvc.perform(patch("/bookings/999?approved=true")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.Error", is("Объект не найден.")));
        mockMvc.perform(patch("/bookings/1?approved=true")
                        .header("X-Sharer-User-Id", 999))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.Error", is("Объект не найден.")));
    }

    @Test
    public void updateAlreadyApprovedTest() throws Exception {
        when(bookingService.reactToBooking(anyInt(), anyBoolean(), anyInt())).thenThrow(illegalArgumentException);
        mockMvc.perform(patch("/bookings/1?approved=true")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.Error", is("Недопустимый аргумент.")));
    }

    @Test
    public void updateWithoutHeaderTest() throws Exception {
        mockMvc.perform(patch("/bookings/1?approved=true")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(correctBooking)))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void getBookingTest() throws Exception {
        when(bookingService.get(anyInt(), anyInt())).thenReturn(bookingDto);
        mockMvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.id", is(booking.getId()), Integer.class))
                .andExpect(jsonPath("$.end", is(booking.getEnd().toString())))
                .andExpect(jsonPath("$.item.id", is(booking.getItem().getId())))
                .andExpect(jsonPath("$.booker.id", is(booking.getBooker().getId())))
                .andExpect(jsonPath("$.status", is(APPROVED.toString())));
    }

    @Test
    public void getBookingNotFoundTest() throws Exception {
        when(bookingService.get(anyInt(), anyInt())).thenThrow(notFoundException);
        mockMvc.perform(get("/bookings/999")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.Error", is("Объект не найден.")));
        mockMvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", 999))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.Error", is("Объект не найден.")));
    }

    @Test
    public void getWithoutHeaderTest() throws Exception {
        mockMvc.perform(get("/bookings/1"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void getAllTest() throws Exception {
        when(bookingService.getAllByOwnerId(anyInt(), anyString(), anyInt(), anyInt())).thenReturn(List.of(bookingDto));
        mockMvc.perform(get("/bookings?state=ALL&from=0&size=10")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    public void getAllUserIdNotFoundTest() throws Exception {
        when(bookingService.getAllByBookerId(anyInt(), any(), anyInt(), anyInt())).thenThrow(notFoundException);
        mockMvc.perform(get("/bookings?state=ALL&from=0&size=10")
                        .header("X-Sharer-User-Id", 999))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.Error", is("Объект не найден.")));
    }

    @Test
    public void getAllUnsupportedStateTest() throws Exception {
        when(bookingService.getAllByBookerId(anyInt(), anyString(), anyInt(), anyInt())).thenThrow(incorrectBookingStatus);
        mockMvc.perform(get("/bookings?state=UNSUPPORTED&from=0&size=10")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.description", is("Неподдерживаемый статус бронирования.")));
    }

    @Test
    public void getAllIdWithoutHeaderTest() throws Exception {
        mockMvc.perform(get("/bookings?state=ALL&from=0&size=10"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void getAllOwnerIdNotFoundTest() throws Exception {
        when(bookingService.getAllByOwnerId(anyInt(), anyString(), anyInt(), anyInt())).thenThrow(notFoundException);

        mockMvc.perform(get("/bookings/owner?state=ALL&from=0&size=10")
                        .header("X-Sharer-User-Id", 999))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.Error", is("Объект не найден.")));
    }

    @Test
    public void getAllWithoutHeaderTest() throws Exception {
        mockMvc.perform(get("/bookings/owner?state=ALL&from=0&size=10"))
                .andExpect(status().is4xxClientError());
    }
}
