package com.softserve.itacademy.service;

import com.softserve.itacademy.dto.userDto.CreateUserDto;
import com.softserve.itacademy.dto.userDto.UpdateUserDto;
import com.softserve.itacademy.dto.userDto.UserDto;
import com.softserve.itacademy.dto.userDto.UserDtoConverter;
import com.softserve.itacademy.model.User;
import com.softserve.itacademy.model.UserRole;
import com.softserve.itacademy.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserDtoConverter userDtoConverter;

    @InjectMocks
    private UserService userService;

    @Test
    public void register_ShouldSetUserRoleAndNoopPrefix() {
        CreateUserDto dto = new CreateUserDto();
        dto.setEmail("new@mail.com");
        dto.setPassword("password");

        User user = new User();
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());

        when(userRepository.findByEmail(dto.getEmail())).thenReturn(Optional.empty());
        when(userDtoConverter.convertToUser(dto)).thenReturn(user);
        when(userRepository.save(any(User.class))).thenReturn(user);

        userService.register(dto);

        assertEquals(UserRole.USER, dto.getRole());
        assertTrue(user.getPassword().startsWith("{noop}"));
        verify(userRepository).save(user);
    }

    @Test
    public void register_ShouldThrowException_WhenEmailAlreadyExists() {
        CreateUserDto dto = new CreateUserDto();
        dto.setEmail("existing@mail.com");
        dto.setPassword("password");

        when(userRepository.findByEmail(dto.getEmail())).thenReturn(Optional.of(new User()));
        lenient().when(userDtoConverter.convertToUser(any(CreateUserDto.class))).thenReturn(new User());

        assertThrows(NullPointerException.class, () -> userService.register(dto));
    }

    @Test
    public void update_ShouldSaveUserWhenAuthorized() {
        UpdateUserDto updateDto = new UpdateUserDto();
        updateDto.setId(1L);
        updateDto.setRole(UserRole.ADMIN);

        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setRole(UserRole.USER);

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenReturn(existingUser);

        userService.update(updateDto);

        verify(userDtoConverter).fillFields(existingUser, updateDto);
        verify(userRepository).save(existingUser);
    }

    @Test
    public void findByIdThrowing_ShouldThrowEntityNotFoundException_WhenIdDoesNotExist() {
        long userId = 99L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.findByIdThrowing(userId));
    }
}