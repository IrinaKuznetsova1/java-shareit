package ru.practicum.shareit.booking;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBooking;
import ru.practicum.shareit.booking.model.BookingRequestState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingControllerTest {
    @MockBean
    private final BookingService bookingService;
    private final ObjectMapper objectMapper;
    private final MockMvc mockMvc;

    private final BookingDto savedBooking = new BookingDto(
            1,
            null,
            null,
            null,
            null,
            BookingStatus.APPROVED);
    private final long userId = 1L;

    @Test
    void create_shouldReturnCreatedBooking() throws Exception {
        NewBooking newBooking = new NewBooking();
        when(bookingService.create(userId, newBooking)).thenReturn(savedBooking);

        MvcResult mvcResult = mockMvc.perform(
                        post("/bookings")
                                .header("X-Sharer-User-Id", userId)
                                .content(objectMapper.writeValueAsString(newBooking))
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();
        String responseBody = mvcResult.getResponse().getContentAsString();
        BookingDto actualBookingDto = objectMapper.readValue(responseBody, BookingDto.class);

        assertThat(actualBookingDto).usingRecursiveComparison().isEqualTo(savedBooking);
    }

    @Test
    void update_shouldReturnApprovedBooking() throws Exception {
        boolean approve = true;
        when(bookingService.update(userId, savedBooking.getId(), approve)).thenReturn(savedBooking);

        MvcResult mvcResult = mockMvc.perform(
                        patch("/bookings/" + savedBooking.getId() + "?approved=" + approve)
                                .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andReturn();
        String responseBody = mvcResult.getResponse().getContentAsString();
        BookingDto actualBookingDto = objectMapper.readValue(responseBody, BookingDto.class);

        assertThat(actualBookingDto).usingRecursiveComparison().isEqualTo(savedBooking);
    }

    @Test
    void findBookingById_shouldReturnBooking() throws Exception {
        when(bookingService.findById(userId, savedBooking.getId())).thenReturn(savedBooking);

        MvcResult mvcResult = mockMvc.perform(
                        get("/bookings/" + savedBooking.getId())
                                .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andReturn();
        String responseBody = mvcResult.getResponse().getContentAsString();
        BookingDto actualBookingDto = objectMapper.readValue(responseBody, BookingDto.class);

        assertThat(actualBookingDto).usingRecursiveComparison().isEqualTo(savedBooking);
    }

    @Test
    void findByBookerId_shouldReturnEmptyList() throws Exception {
        when(bookingService.findByBookerId(userId, BookingRequestState.ALL)).thenReturn(Collections.emptyList());

        mockMvc.perform(
                        get("/bookings?state=" + BookingRequestState.ALL)
                                .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void findByBookerId_shouldReturnListBookings() throws Exception {
        when(bookingService.findByBookerId(userId, BookingRequestState.ALL)).thenReturn(Collections.singletonList(savedBooking));

        MvcResult mvcResult = mockMvc.perform(
                        get("/bookings?state=" + BookingRequestState.ALL)
                                .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andReturn();
        String responseBody = mvcResult.getResponse().getContentAsString();
        List<BookingDto> actualBookingsDto = objectMapper.readValue(responseBody, new TypeReference<List<BookingDto>>() {
        });

        assertThat(actualBookingsDto.getFirst()).usingRecursiveComparison().isEqualTo(savedBooking);
    }

    @Test
    void findByOwnerId_shouldReturnEmptyList() throws Exception {
        when(bookingService.findByOwnerId(userId, BookingRequestState.ALL)).thenReturn(Collections.emptyList());

        mockMvc.perform(
                        get("/bookings/owner?state=" + BookingRequestState.ALL)
                                .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void findByOwnerId_shouldReturnListBookings() throws Exception {
        when(bookingService.findByOwnerId(userId, BookingRequestState.ALL)).thenReturn(Collections.singletonList(savedBooking));

        MvcResult mvcResult = mockMvc.perform(
                        get("/bookings/owner?state=" + BookingRequestState.ALL)
                                .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andReturn();
        String responseBody = mvcResult.getResponse().getContentAsString();
        List<BookingDto> actualBookingsDto = objectMapper.readValue(responseBody, new TypeReference<List<BookingDto>>() {
        });

        assertThat(actualBookingsDto.getFirst()).usingRecursiveComparison().isEqualTo(savedBooking);
    }
}