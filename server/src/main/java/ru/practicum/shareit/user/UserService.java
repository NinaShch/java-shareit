package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;

public interface UserService {
    UserDto addUser(UserDto userDto);

    UserDto updateUser(UserDto userDto, Long userId);

    Collection<UserDto> getAllUsers();

    UserDto getUserById(Long userId);

    void deleteUserById(Long userId);
}
