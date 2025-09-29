package ru.practicum.shareit.request;

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
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.NewItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemRequestController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRequestControllerTest {
    @MockBean
    private final ItemRequestService itemRequestService;
    private final ObjectMapper objectMapper;
    private final MockMvc mockMvc;

    private final ItemRequestDto savedRequest = new ItemRequestDto(
            1,
            "desc",
            null,
            null);
    private final long userId = 1L;

    @Test
    void create_shouldReturnCreatedRequest() throws Exception {
        NewItemRequest newRequest = new NewItemRequest();
        when(itemRequestService.create(userId, newRequest)).thenReturn(savedRequest);

        MvcResult mvcResult = mockMvc.perform(
                        post("/requests")
                                .header("X-Sharer-User-Id", userId)
                                .content(objectMapper.writeValueAsString(newRequest))
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();
        String responseBody = mvcResult.getResponse().getContentAsString();
        ItemRequestDto actualRequestDto = objectMapper.readValue(responseBody, ItemRequestDto.class);

        assertThat(actualRequestDto).usingRecursiveComparison().isEqualTo(savedRequest);
    }

    @Test
    void findItemRequestsByUserId_shouldReturnEmptyList() throws Exception {
        when(itemRequestService.findItemRequestsByUserId((userId))).thenReturn(Collections.emptyList());

        mockMvc.perform(
                        get("/requests")
                                .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void findItemRequestsByUserId_shouldReturnUsersRequests() throws Exception {
        when(itemRequestService.findItemRequestsByUserId((userId))).thenReturn(Collections.singletonList(savedRequest));

        MvcResult mvcResult = mockMvc.perform(
                        get("/requests")
                                .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andReturn();
        String responseBody = mvcResult.getResponse().getContentAsString();
        List<ItemRequestDto> actualRequetsDto = objectMapper.readValue(responseBody, new TypeReference<List<ItemRequestDto>>() {
        });

        assertThat(actualRequetsDto.getFirst()).usingRecursiveComparison().isEqualTo(savedRequest);
    }

    @Test
    void findOtherUsersItemRequests_shouldReturnEmptyList() throws Exception {
        when(itemRequestService.findOtherUsersItemRequests((userId))).thenReturn(Collections.emptyList());

        mockMvc.perform(
                        get("/requests/all")
                                .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void findOtherUsersItemRequests_shouldReturnUsersRequests() throws Exception {
        when(itemRequestService.findOtherUsersItemRequests(userId)).thenReturn(Collections.singletonList(savedRequest));

        MvcResult mvcResult = mockMvc.perform(
                        get("/requests/all")
                                .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andReturn();
        String responseBody = mvcResult.getResponse().getContentAsString();
        List<ItemRequestDto> actualRequetsDto = objectMapper.readValue(responseBody, new TypeReference<List<ItemRequestDto>>() {
        });

        assertThat(actualRequetsDto.getFirst()).usingRecursiveComparison().isEqualTo(savedRequest);
    }

    @Test
    void findItemRequestById_shouldReturnRequest() throws Exception {
        when(itemRequestService.findById(savedRequest.getId())).thenReturn(savedRequest);

        MvcResult mvcResult = mockMvc.perform(
                        get("/requests/" + savedRequest.getId()))
                .andExpect(status().isOk())
                .andReturn();
        String responseBody = mvcResult.getResponse().getContentAsString();
        ItemRequestDto actualRequestDto = objectMapper.readValue(responseBody, ItemRequestDto.class);

        assertThat(actualRequestDto).usingRecursiveComparison().isEqualTo(savedRequest);
    }
}