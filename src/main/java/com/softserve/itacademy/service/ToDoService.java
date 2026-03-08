package com.softserve.itacademy.service;

import com.softserve.itacademy.config.exception.NullEntityReferenceException;
import com.softserve.itacademy.model.ToDo;
import com.softserve.itacademy.model.User;
import com.softserve.itacademy.repository.ToDoRepository;
import com.softserve.itacademy.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
public class ToDoService {
    private final ToDoRepository todoRepository;
    private final UserRepository userRepository;

    @Transactional
    public ToDo create(ToDo todo) {
        log.info("Creating new ToDo with title: {}", todo.getTitle());
        if (todo != null) {
            if (todoRepository.existsByTitle(todo.getTitle())) {
                log.warn("Creation failed: ToDo title '{}' already exists", todo.getTitle());
                throw new IllegalArgumentException("ToDo with title '" + todo.getTitle() + "' already exists");
            }
            ToDo savedToDo = todoRepository.save(todo);
            log.debug("ToDo created with ID: {}", savedToDo.getId());
            return savedToDo;
        }
        log.error("Attempted to create a null ToDo reference");
        throw new NullEntityReferenceException("ToDo cannot be 'null'");
    }

    @Transactional(readOnly = true)
    public ToDo readById(long id) {
        return todoRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("ToDo with id " + id + " not found"));
    }

    @Transactional
    public void delete(long id) {
        ToDo todo = readById(id);
        todoRepository.delete(todo);
    }

    @Transactional(readOnly = true)
    public List<ToDo> getAll() {
        return todoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<ToDo> getByUserId(long userId) {
        return todoRepository.getByUserId(userId);
    }

    @Transactional
    public ToDo update(ToDo todo) {
        if (todo != null) {
            if (todoRepository.existsByTitleAndIdNot(todo.getTitle(), todo.getId())) {
                throw new IllegalArgumentException("ToDo with title '" + todo.getTitle() + "' already exists");
            }
            readById(todo.getId());
            return todoRepository.save(todo);
        }
        throw new NullEntityReferenceException("ToDo cannot be 'null'");
    }

    @Transactional
    public void addCollaborator(long todoId, long userId) {
        log.info("Adding user ID: {} as collaborator to ToDo ID: {}", userId, todoId);
        ToDo todo = readById(todoId);
        User collaborator = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("Failed to add collaborator: User ID {} not found", userId);
                    return new EntityNotFoundException("User not found");
                });
        todo.getCollaborators().add(collaborator);
        log.debug("User {} added to collaborators of ToDo {}", userId, todoId);
    }

    @Transactional
    public void removeCollaborator(long todoId, long userId) {
        log.info("Removing collaborator userId={} from todoId={}", userId, todoId);
        ToDo todo = readById(todoId);
        User collaborator = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("User with id {} not found when removing collaborator", userId);
                    return new EntityNotFoundException("User with id " + userId + " not found");
                });
        todo.getCollaborators().remove(collaborator);
        update(todo);
        log.info("Collaborator userId={} removed from todoId={} successfully", userId, todoId);
    }
}