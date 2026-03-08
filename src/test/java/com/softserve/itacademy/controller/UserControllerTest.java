package com.softserve.itacademy.controller;

import com.softserve.itacademy.model.User;
import com.softserve.itacademy.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {
    @Autowired private MockMvc mockMvc;
    @MockBean private UserService userService;

    @Test
    void getAll_ShouldDisplayUsersList() throws Exception {
        when(userService.getAll()).thenReturn(List.of(new User()));
        mockMvc.perform(get("/users/all"))
                .andExpect(status().isOk())
                .andExpect(view().name("users-list"))
                .andExpect(model().attributeExists("users"));
    }

    @Test
    void createPost_ShouldRedirectOnSuccess() throws Exception {
        mockMvc.perform(post("/users/create")
                        .param("firstName", "John")
                        .param("lastName", "Doe")
                        .param("email", "john@mail.com")
                        .param("password", "Qwerty123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/users/all"));
    }
}
