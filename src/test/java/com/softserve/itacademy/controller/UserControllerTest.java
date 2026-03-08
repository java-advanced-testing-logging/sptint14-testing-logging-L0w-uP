package com.softserve.itacademy.controller;

import com.softserve.itacademy.model.User;
import com.softserve.itacademy.model.UserRole;
import com.softserve.itacademy.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    public void getAllUsers_ShouldReturnUserList() throws Exception {
        User user1 = new User();
        user1.setId(1L);
        user1.setFirstName("John");
        user1.setLastName("Doe");
        user1.setEmail("john@mail.com");
        user1.setRole(UserRole.USER);

        User user2 = new User();
        user2.setId(2L);
        user2.setFirstName("Jane");
        user2.setLastName("Smith");
        user2.setEmail("jane@mail.com");
        user2.setRole(UserRole.ADMIN);

        when(userService.getAll()).thenReturn(List.of(user1, user2));

        mockMvc.perform(get("/users/all"))
                .andExpect(status().isOk())
                .andExpect(view().name("users-list"))
                .andExpect(model().attributeExists("users"))
                .andExpect(model().attribute("users", List.of(user1, user2)));
    }

    @Test
    public void getUserInfo_ShouldReturnUserView_WhenIdExists() throws Exception {
        long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setFirstName("John");
        user.setEmail("john@mail.com");

        when(userService.readById(userId)).thenReturn(user);

        mockMvc.perform(get("/users/" + userId + "/read"))
                .andExpect(status().isOk())
                .andExpect(view().name("user-info"))
                .andExpect(model().attribute("user", user));
    }

    @Test
    public void create_ShouldReturnCreateForm() throws Exception {
        mockMvc.perform(get("/users/create"))
                .andExpect(status().isOk())
                .andExpect(view().name("create-user"))
                .andExpect(model().attributeExists("user"));
    }
}