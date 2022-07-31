package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;
import java.util.Optional;

public interface UserStorage {
    User add(UserDto userDto);

    User update(UserDto userDto, Long userId);

    Collection<User> getAll();

    void deleteById(Long userId);

    Optional<User> getById(Long id);
}
