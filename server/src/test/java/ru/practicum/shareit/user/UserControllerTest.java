package ru.practicum.shareit.user;

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
import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserControllerTest {
    @MockBean
    private final UserService userService;
    private final ObjectMapper objectMapper;
    private final MockMvc mockMvc;

    private final UserDto savedUser = new UserDto(1, "email@email.com", "name");

    @Test
    void findAll_shouldReturnEmptyList() throws Exception {
        when(userService.findAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(
                        get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void findAll_shouldReturnAllUsers() throws Exception {
        when(userService.findAll()).thenReturn(Collections.singletonList(savedUser));

        MvcResult mvcResult = mockMvc.perform(
                        get("/users"))
                .andExpect(status().isOk())
                .andReturn();
        String responseBody = mvcResult.getResponse().getContentAsString();
        List<UserDto> actualUserDto = objectMapper.readValue(responseBody, new TypeReference<List<UserDto>>() {
        });

        assertThat(actualUserDto.getFirst()).usingRecursiveComparison().isEqualTo(savedUser);
    }

    @Test
    void findUserById_shouldReturnUserWithId() throws Exception {
        when(userService.findById(savedUser.getId())).thenReturn(savedUser);

        MvcResult mvcResult = mockMvc.perform(
                        get("/users/" + savedUser.getId()))
                .andExpect(status().isOk())
                .andReturn();
        String responseBody = mvcResult.getResponse().getContentAsString();
        UserDto actualUserDto = objectMapper.readValue(responseBody, UserDto.class);

        assertTrue(actualUserDto.getId() > 0L);
        assertThat(actualUserDto).usingRecursiveComparison().ignoringFields("id").isEqualTo(savedUser);
    }

    @Test
    void createUser_shouldReturnCreatedUser() throws Exception {
        NewUserRequest newUser = new NewUserRequest();
        newUser.setName("name");
        newUser.setEmail("email@email.com");
        when(userService.create(newUser)).thenReturn(savedUser);

        MvcResult mvcResult = mockMvc.perform(
                        post("/users")
                                .content(objectMapper.writeValueAsString(newUser))
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();
        String responseBody = mvcResult.getResponse().getContentAsString();
        UserDto actualUserDto = objectMapper.readValue(responseBody, UserDto.class);

        assertTrue(actualUserDto.getId() > 0L);
        assertThat(actualUserDto).usingRecursiveComparison().ignoringFields("id").isEqualTo(newUser);
    }

    @Test
    void updateUser_shouldReturnUpdatedUser() throws Exception {
        UpdateUserRequest updUser = new UpdateUserRequest();
        updUser.setEmail("email@email.com");
        updUser.setName("name");
        when(userService.update(savedUser.getId(), updUser)).thenReturn(savedUser);

        MvcResult mvcResult = mockMvc.perform(
                        patch("/users/" + savedUser.getId())
                                .content(objectMapper.writeValueAsString(updUser))
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        String responseBody = mvcResult.getResponse().getContentAsString();
        UserDto actualUserDto = objectMapper.readValue(responseBody, UserDto.class);

        assertTrue(actualUserDto.getId() > 0L);
        assertThat(actualUserDto).usingRecursiveComparison().ignoringFields("id").isEqualTo(updUser);
    }

    @Test
    void deleteUser_shouldReturnNoContent() throws Exception {
        doNothing().when(userService).deleteUser(savedUser.getId());
        mockMvc.perform(
                        delete("/users/" + savedUser.getId()))
                .andExpect(status().isOk());
    }
}