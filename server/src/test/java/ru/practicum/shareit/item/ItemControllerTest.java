package ru.practicum.shareit.item;

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
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.service.ItemService;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(ItemController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemControllerTest {
    @MockBean
    private final ItemService itemService;
    private final ObjectMapper objectMapper;
    private final MockMvc mockMvc;

    private final ItemDto savedItem = new ItemDto(1, "name", "desc", true);
    private final ItemWithDatesDto savedItemWithDatesDto = new ItemWithDatesDto(
            1,
            "name",
            "desc",
            true,
            null,
            null,
            null);
    private final long userId = 1L;

    @Test
    void findItemsByUserId_shouldReturnEmptyList() throws Exception {
        when(itemService.findItemsByOwnerId(userId)).thenReturn(Collections.emptyList());

        mockMvc.perform(
                        get("/items")
                                .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void findItemsByUserId_shouldReturnUsersItems() throws Exception {
        when(itemService.findItemsByOwnerId(userId)).thenReturn(Collections.singletonList(savedItemWithDatesDto));

        MvcResult mvcResult = mockMvc.perform(
                        get("/items")
                                .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andReturn();
        String responseBody = mvcResult.getResponse().getContentAsString();
        List<ItemWithDatesDto> actualItemsDto = objectMapper.readValue(responseBody, new TypeReference<List<ItemWithDatesDto>>() {
        });

        assertThat(actualItemsDto.getFirst()).usingRecursiveComparison().isEqualTo(savedItemWithDatesDto);
    }

    @Test
    void findItemById_shouldReturnItem() throws Exception {
        when(itemService.findById(savedItem.getId())).thenReturn(savedItemWithDatesDto);

        MvcResult mvcResult = mockMvc.perform(
                        get("/items/" + savedItem.getId()))
                .andExpect(status().isOk())
                .andReturn();
        String responseBody = mvcResult.getResponse().getContentAsString();
        ItemDto actualItemDto = objectMapper.readValue(responseBody, ItemDto.class);

        assertThat(actualItemDto).usingRecursiveComparison().isEqualTo(savedItem);
    }

    @Test
    void create_shouldReturnCreatedItem() throws Exception {
        NewItem newItem = new NewItem();
        when(itemService.create(userId, newItem)).thenReturn(savedItem);

        MvcResult mvcResult = mockMvc.perform(
                        post("/items")
                                .header("X-Sharer-User-Id", userId)
                                .content(objectMapper.writeValueAsString(newItem))
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();
        String responseBody = mvcResult.getResponse().getContentAsString();
        ItemDto actualItemDto = objectMapper.readValue(responseBody, ItemDto.class);

        assertThat(actualItemDto).usingRecursiveComparison().isEqualTo(savedItem);
    }

    @Test
    void createComment_shouldReturnCreatedComment() throws Exception {
        NewCommentRequest newComment = new NewCommentRequest();
        newComment.setText("text");
        CommentDto savedComment = new CommentDto(1, "text", "authorName", null);
        when(itemService.createComment(userId, savedItem.getId(), newComment)).thenReturn(savedComment);

        MvcResult mvcResult = mockMvc.perform(
                        post("/items/" + savedItem.getId() + "/comment")
                                .header("X-Sharer-User-Id", userId)
                                .content(objectMapper.writeValueAsString(newComment))
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();
        String responseBody = mvcResult.getResponse().getContentAsString();
        CommentDto actualCommentDto = objectMapper.readValue(responseBody, CommentDto.class);

        assertThat(actualCommentDto).usingRecursiveComparison().isEqualTo(savedComment);
    }

    @Test
    void update_shouldReturnUpdatedItem() throws Exception {
        UpdateItemRequest updateItem = new UpdateItemRequest();
        when(itemService.update(userId, savedItem.getId(), updateItem)).thenReturn(savedItem);

        MvcResult mvcResult = mockMvc.perform(
                        patch("/items/" + savedItem.getId())
                                .header("X-Sharer-User-Id", userId)
                                .content(objectMapper.writeValueAsString(updateItem))
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        String responseBody = mvcResult.getResponse().getContentAsString();
        ItemDto actualItemDto = objectMapper.readValue(responseBody, ItemDto.class);

        assertThat(actualItemDto).usingRecursiveComparison().isEqualTo(savedItem);
    }

    @Test
    void searchItems_shouldReturnItem() throws Exception {
        String text = "name";
        when(itemService.searchItems(text)).thenReturn(Collections.singletonList(savedItem));

        mockMvc.perform(
                        get("/items/search?text=" + text))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(savedItem.getId()))
                .andExpect(jsonPath("$[0].name").value(savedItem.getName()))
                .andExpect(jsonPath("$[0].description").value(savedItem.getDescription()))
                .andExpect(jsonPath("$[0].available").value(savedItem.isAvailable()));
    }
}