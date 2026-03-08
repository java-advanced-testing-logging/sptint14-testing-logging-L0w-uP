package com.softserve.itacademy.service;

import com.softserve.itacademy.model.ToDo;
import com.softserve.itacademy.model.User;
import com.softserve.itacademy.repository.ToDoRepository;
import com.softserve.itacademy.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ToDoServiceTest {

    @Mock
    private ToDoRepository todoRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ToDoService todoService;

    @Test
    public void create_ShouldSaveToDo_WhenValid() {
        ToDo todo = new ToDo();
        todo.setTitle("New ToDo");

        when(todoRepository.existsByTitle("New ToDo")).thenReturn(false);
        when(todoRepository.save(any(ToDo.class))).thenReturn(todo);

        ToDo result = todoService.create(todo);

        assertNotNull(result);
        assertEquals("New ToDo", result.getTitle());
        verify(todoRepository).save(todo);
    }

    @Test
    public void create_ShouldThrowException_WhenTitleAlreadyExists() {
        ToDo todo = new ToDo();
        todo.setTitle("Existing Title");

        when(todoRepository.existsByTitle("Existing Title")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> todoService.create(todo));
    }

    @Test
    public void readById_ShouldReturnToDo_WhenIdExists() {
        long id = 1L;
        ToDo todo = new ToDo();
        todo.setId(id);

        when(todoRepository.findById(id)).thenReturn(Optional.of(todo));

        ToDo result = todoService.readById(id);

        assertNotNull(result);
        assertEquals(id, result.getId());
    }

    @Test
    public void readById_ShouldThrowEntityNotFoundException_WhenIdDoesNotExist() {
        long id = 99L;
        when(todoRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> todoService.readById(id));
    }

    @Test
    public void update_ShouldSaveUpdatedToDo_WhenValid() {
        ToDo todo = new ToDo();
        todo.setId(1L);
        todo.setTitle("Updated Title");

        when(todoRepository.existsByTitleAndIdNot("Updated Title", 1L)).thenReturn(false);
        when(todoRepository.findById(1L)).thenReturn(Optional.of(todo));
        when(todoRepository.save(any(ToDo.class))).thenReturn(todo);

        ToDo result = todoService.update(todo);

        assertEquals("Updated Title", result.getTitle());
        verify(todoRepository).save(todo);
    }

    @Test
    public void delete_ShouldCallRepository_WhenIdExists() {
        long id = 1L;
        ToDo todo = new ToDo();
        todo.setId(id);

        when(todoRepository.findById(id)).thenReturn(Optional.of(todo));

        todoService.delete(id);

        verify(todoRepository).delete(todo);
    }

    @Test
    public void addCollaborator_ShouldUpdateToDo_WhenUserExists() {
        long todoId = 1L;
        long userId = 2L;

        ToDo todo = new ToDo();
        todo.setId(todoId);
        todo.setCollaborators(new java.util.HashSet<>());

        User user = new User();
        user.setId(userId);

        when(todoRepository.findById(anyLong())).thenReturn(Optional.of(todo));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(todoRepository.save(any(ToDo.class))).thenReturn(todo);

        todoService.addCollaborator(todoId, userId);

        assertTrue(todo.getCollaborators().contains(user));
        verify(todoRepository, times(1)).save(todo);
    }

    @Test
    public void getByUserId_ShouldReturnList() {
        long userId = 1L;
        List<ToDo> todos = List.of(new ToDo(), new ToDo());

        when(todoRepository.getByUserId(userId)).thenReturn(todos);

        List<ToDo> result = todoService.getByUserId(userId);

        assertEquals(2, result.size());
    }
}