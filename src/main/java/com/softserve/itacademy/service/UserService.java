package com.softserve.itacademy.service;

import com.softserve.itacademy.config.exception.NullEntityReferenceException;
import com.softserve.itacademy.dto.userDto.CreateUserDto;
import com.softserve.itacademy.dto.userDto.UpdateUserDto;
import com.softserve.itacademy.dto.userDto.UserDto;
import com.softserve.itacademy.dto.userDto.UserDtoConverter;
import com.softserve.itacademy.model.User;
import com.softserve.itacademy.model.UserRole;
import com.softserve.itacademy.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final UserDtoConverter userDtoConverter;

    @Transactional
    public User register(CreateUserDto createUserDto) {
        log.info("Registering new user with email: {}", createUserDto.getEmail());
        createUserDto.setRole(UserRole.USER);
        User user = userDtoConverter.convertToUser(createUserDto);
        user.setPassword("{noop}" + user.getPassword());
        return create(user);
    }

    @Transactional
    public User create(User user) {
        if (user != null) {
            if (userRepository.findByEmail(user.getEmail()).isPresent()) {
                log.warn("Registration failed: Email {} already exists", user.getEmail());
                throw new IllegalArgumentException("User with email '" + user.getEmail() + "' already exists");
            }
            User savedUser = userRepository.save(user);
            log.debug("User created successfully with ID: {}", savedUser.getId());
            return savedUser;
        }
        log.error("Attempted to create a null user reference");
        throw new NullEntityReferenceException("User cannot be 'null'");
    }

    @Transactional
    public UserDto update(UpdateUserDto updateUserDto) {
        log.info("Updating user ID: {}", updateUserDto.getId());
        User user = userRepository.findById(updateUserDto.getId())
                .orElseThrow(() -> {
                    log.error("Update failed: User with ID {} not found", updateUserDto.getId());
                    return new EntityNotFoundException("User not found");
                });
        userDtoConverter.fillFields(user, updateUserDto);
        userRepository.save(user);
        log.debug("User ID: {} successfully updated", user.getId());
        return userDtoConverter.toDto(user);
    }

    @Transactional(readOnly = true)
    public User readById(long id) {
        return userRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("User with id " + id + " not found"));
    }

    @Transactional
    public void delete(long id) {
        log.info("Deleting user ID: {}", id);
        User user = readById(id);
        userRepository.delete(user);
        log.debug("User ID: {} deleted", id);
    }

    @Transactional(readOnly = true)
    public List<User> getAll() {
        return userRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<User> findByUsername(String username) {
        return userRepository.findByEmail(username);
    }

    @Transactional(readOnly = true)
    public Optional<UserDto> findById(long id) {
        return userRepository.findById(id).map(userDtoConverter::toDto);
    }

    @Transactional(readOnly = true)
    public UserDto findByIdThrowing(long id) {
        return userRepository.findById(id).map(userDtoConverter::toDto).orElseThrow(EntityNotFoundException::new);
    }

    @Transactional(readOnly = true)
    public List<UserDto> findAll() {
        return userRepository.findAll().stream().map(userDtoConverter::toDto).toList();
    }
}