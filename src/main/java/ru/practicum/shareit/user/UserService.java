package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.UserBadRequestException;
import ru.practicum.shareit.exception.UserDataConflictException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.storage.InMemoryUserStorage;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final InMemoryUserStorage inMemoryUserStorage;

    public UserDto addUser(UserDto userDto) {
        if (userDto.getEmail() == null || userDto.getName() == null)
            throw new UserBadRequestException("Attempt to add user with empty fields");
        return UserMapper.toUserDto(inMemoryUserStorage.add(UserMapper.toUser(userDto)));
    }

    public UserDto updateUser(UserDto userDto, Long userId) {
        return UserMapper.toUserDto(inMemoryUserStorage.update(UserMapper.toUser(userDto), userId));
    }

    public Collection<UserDto> getAllUsers() {
        return inMemoryUserStorage.getAll().stream().map(UserMapper::toUserDto).collect(Collectors.toList());
    }

    public UserDto getUserById(Long userId) {
        return UserMapper.toUserDto(inMemoryUserStorage.getById(userId)
                .orElseThrow(() -> new UserDataConflictException("Attempt to get user by absent id")));
    }

    public void deleteUserById(Long userId) {
        inMemoryUserStorage.deleteById(userId);
    }
}
