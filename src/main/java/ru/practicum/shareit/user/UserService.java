package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.UserDataConflictException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.storage.InMemoryUserStorage;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class UserService {

    private final InMemoryUserStorage inMemoryUserStorage;

    public User addUser(UserDto userDto) {
        return inMemoryUserStorage.add(userDto);
    }

    public User updateUser(UserDto userDto, Long userId) {
        return inMemoryUserStorage.update(userDto, userId);
    }

    public Collection<User> getAllUsers() {
        return inMemoryUserStorage.getAll();
    }

    public User getUserById(Long userId) {
        return inMemoryUserStorage.getById(userId)
                .orElseThrow(() -> new UserDataConflictException("Attempt to get user by absent id"));
    }

    public void deleteUserById(Long userId) {
        inMemoryUserStorage.deleteById(userId);
    }
}
