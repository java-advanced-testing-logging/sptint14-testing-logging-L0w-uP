package com.softserve.itacademy.service;

import com.softserve.itacademy.dto.userDto.CreateUserDto;
import com.softserve.itacademy.dto.userDto.UserDtoConverter;
import com.softserve.itacademy.model.User;
import com.softserve.itacademy.model.UserRole;
import com.softserve.itacademy.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock private UserRepository userRepository;
    @Mock private UserDtoConverter userDtoConverter;
    @InjectMocks private UserService userService;

    @Test
    void register_ShouldSetUserRoleAndNoopPrefix() {
        CreateUserDto dto = new CreateUserDto();
        dto.setPassword("password");
        User user = new User();
        user.setEmail("test@mail.com");

        when(userDtoConverter.convertToUser(dto)).thenReturn(user);
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(user);

        userService.register(dto);

        assertEquals(UserRole.USER, dto.getRole());
        assertTrue(user.getPassword().startsWith("{noop}"));
        verify(userRepository).save(user);
    }

    @Test
    void register_ShouldThrowException_WhenEmailExists() {
        CreateUserDto dto = new CreateUserDto();
        dto.setEmail("exists@mail.com");
        when(userRepository.findByEmail(dto.getEmail())).thenReturn(Optional.of(new User()));

        assertThrows(IllegalArgumentException.class, () -> userService.register(dto));
    }
}
