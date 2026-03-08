package com.softserve.itacademy.service;

import com.softserve.itacademy.model.ToDo;
import com.softserve.itacademy.model.User;
import com.softserve.itacademy.repository.ToDoRepository;
import com.softserve.itacademy.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ToDoServiceTest {
    @Mock private ToDoRepository todoRepository;
    @Mock private UserRepository userRepository;
    @InjectMocks private ToDoService todoService;

    @Test
    void addCollaborator_ShouldAddUserToSet() {
        User user = new User();
        user.setId(1L);
        ToDo todo = new ToDo();
        todo.setCollaborators(new HashSet<>());

        when(todoRepository.findById(1L)).thenReturn(Optional.of(todo));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(todoRepository.save(any())).thenReturn(todo);

        todoService.addCollaborator(1L, 1L);

        assertTrue(todo.getCollaborators().contains(user));
    }
}