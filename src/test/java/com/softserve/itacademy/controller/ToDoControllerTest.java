package com.softserve.itacademy.controller;

import com.softserve.itacademy.model.ToDo;
import com.softserve.itacademy.model.User;
import com.softserve.itacademy.service.ToDoService;
import com.softserve.itacademy.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ToDoController.class)
public class ToDoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ToDoService todoService;

    @MockBean
    private UserService userService;


    @Test
    public void getAllUserTodosTest() throws Exception {
        long userId = 1L;
        when(todoService.getByUserId(userId)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/todos/all/users/" + userId))
                .andExpect(status().isOk())
                .andExpect(view().name("todos-user"))
                .andExpect(model().attributeExists("todos"))
                .andExpect(model().attributeExists("user"));
    }

    @Test
    public void getTasksTest() throws Exception {
        long todoId = 1L;
        User owner = new User();
        owner.setId(2L);

        ToDo todo = new ToDo();
        todo.setId(todoId);
        todo.setOwner(owner);
        todo.setCollaborators(Collections.emptySet());
        todo.setTasks(Collections.emptySet());

        when(todoService.readById(todoId)).thenReturn(todo);
        when(userService.getAll()).thenReturn(List.of(owner));

        mockMvc.perform(get("/todos/" + todoId + "/tasks"))
                .andExpect(status().isOk())
                .andExpect(view().name("todo-tasks"))
                .andExpect(model().attributeExists("todo"))
                .andExpect(model().attributeExists("tasks"))
                .andExpect(model().attributeExists("users"));
    }
}