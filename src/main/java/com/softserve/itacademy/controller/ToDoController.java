package com.softserve.itacademy.controller;

import com.softserve.itacademy.dto.todoDto.CreateToDoDto;
import com.softserve.itacademy.dto.todoDto.ToDoDtoConverter;
import com.softserve.itacademy.dto.todoDto.UpdateToDoDto;
import com.softserve.itacademy.model.ToDo;
import com.softserve.itacademy.service.ToDoService;
import com.softserve.itacademy.model.User;
import com.softserve.itacademy.service.TaskService;
import com.softserve.itacademy.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/todos")
@RequiredArgsConstructor
public class ToDoController {

    private final ToDoService todoService;
    private final TaskService taskService;
    private final UserService userService;
    private final ToDoDtoConverter todoDtoConverter;

    @GetMapping("/create/users/{owner_id}")
    public String create(@PathVariable("owner_id") Long ownerId, Model model) {
        model.addAttribute("todo", new CreateToDoDto());
        model.addAttribute("ownerId", ownerId);
        return "create-todo";
    }

    @PostMapping("/create/users/{owner_id}")
    public String create(@PathVariable("owner_id") Long ownerId,
                         @Validated @ModelAttribute("todo") CreateToDoDto todoDto,
                         BindingResult result) {
        if (result.hasErrors()) {
            return "create-todo";
        }
        User owner = userService.readById(ownerId);
        ToDo todo = todoDtoConverter.toEntity(todoDto, owner);
        todo.setCreatedAt(LocalDateTime.now());
        todoService.create(todo);
        return "redirect:/todos/all/users/" + ownerId;
    }

    @GetMapping("/{id}/update/users/{owner_id}")
    public String update(@PathVariable("id") Long id,
                         @PathVariable("owner_id") Long ownerId, Model model) {
        ToDo todo = todoService.readById(id);
        UpdateToDoDto updateToDoDto = UpdateToDoDto.builder()
                .id(todo.getId())
                .title(todo.getTitle())
                .ownerId(todo.getOwner().getId())
                .build();
        model.addAttribute("todo", updateToDoDto);
        return "update-todo";
    }

    @PostMapping("/{id}/update/users/{owner_id}")
    public String update(@PathVariable("id") Long id,
                         @PathVariable("owner_id") Long ownerId,
                         @Validated @ModelAttribute("todo") UpdateToDoDto todoDto,
                         BindingResult result) {
        if (result.hasErrors()) {
            return "update-todo";
        }
        ToDo todo = todoService.readById(id);
        User owner = userService.readById(ownerId);
        todoDtoConverter.fillFields(todo, todoDto, owner);
        todoService.update(todo);
        return "redirect:/todos/all/users/" + ownerId;
    }

    @GetMapping("/{id}/delete/users/{owner_id}")
    public String delete(@PathVariable("id") Long id,
                         @PathVariable("owner_id") Long ownerId) {
        todoService.delete(id);
        return "redirect:/todos/all/users/" + ownerId;
    }

    @GetMapping("/all/users/{user_id}")
    public String getAll(@PathVariable("user_id") Long userId, Model model) {
        List<ToDo> todos = todoService.getByUserId(userId);
        model.addAttribute("todos", todos);
        model.addAttribute("user", userService.readById(userId));
        return "todos-user";
    }

    @GetMapping("/{id}/tasks")
    public String getTasks(@PathVariable("id") Long todoId, Model model) {
        ToDo todo = todoService.readById(todoId);
        model.addAttribute("todo", todo);
        model.addAttribute("tasks", todo.getTasks());
        model.addAttribute("users", userService.getAll().stream()
                .filter(user -> !todo.getOwner().equals(user) && !todo.getCollaborators().contains(user))
                .collect(Collectors.toList()));
        return "todo-tasks";
    }

    @GetMapping("/{id}/add")
    public String addCollaborator(@PathVariable("id") Long todoId,
                                  @RequestParam("user_id") Long userId) {
        todoService.addCollaborator(todoId, userId);
        return "redirect:/todos/" + todoId + "/tasks";
    }

    @GetMapping("/{id}/remove")
    public String removeCollaborator(@PathVariable("id") Long todoId,
                                     @RequestParam("user_id") Long userId) {
        todoService.removeCollaborator(todoId, userId);
        return "redirect:/todos/" + todoId + "/tasks";
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ModelAndView handleEntityNotFoundException(EntityNotFoundException ex) {
        ModelAndView modelAndView = new ModelAndView("error/404");
        modelAndView.addObject("message", ex.getMessage());
        return modelAndView;
    }
}