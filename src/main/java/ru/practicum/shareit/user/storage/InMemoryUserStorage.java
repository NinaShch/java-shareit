package ru.practicum.shareit.user.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.UserBadRequestException;
import ru.practicum.shareit.exception.UserDataConflictException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.*;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {

    private final Map<Long, User> usersMap = new HashMap<>();
    private Long id = 0L;

    private Long getNextId() {
        return ++id;
    }

    @Override
    public User add(UserDto userDto) {
        if (userDto.getEmail() == null || userDto.getName() == null)
            throw new UserBadRequestException("Attempt to add user with empty fields");
        if (usersMap.values().stream().noneMatch(user -> Objects.equals(userDto.getEmail(), user.getEmail()))) {
            Long userId = getNextId();
            log.info("New user added, userId = {}, user = {}", userId, userDto);
            User user = UserMapper.toUser(userDto, userId);
            usersMap.put(userId, user);
            return user;
        } else {
            throw new UserDataConflictException("Attempt to create user with existing email");
        }
    }

    @Override
    public User update(UserDto userDto, Long userId) {
        if (!usersMap.containsKey(userId)) throw new UserBadRequestException("Attempt to update user with absent id");
        if (usersMap.values().stream()
                .filter(user -> !Objects.equals(user.getId(), userId))
                .anyMatch(user -> Objects.equals(userDto.getEmail(), user.getEmail())))
            throw new UserDataConflictException("Attempt to update user email where email is already exists");
        User userUpdate = usersMap.get(userId);
        User user = UserMapper.toUser(userDto, userId);
        if (user.getName() == null) user.setName(userUpdate.getName());
        if (user.getEmail() == null) user.setEmail(userUpdate.getEmail());
        usersMap.put(userId, user);
        log.info("User id = {} is updated", userId);
        return user;
    }

    @Override
    public Collection<User> getAll() {
        return usersMap.values();
    }

    @Override
    public void deleteById(Long userId) {
        if (!usersMap.containsKey(userId)) throw new UserBadRequestException("Attempt to delete user with absent id");
        usersMap.remove(userId);
        log.info("user id = {} is deleted", userId);
    }

    @Override
    public Optional<User> getById(Long id) {
        return Optional.ofNullable(usersMap.get(id));
    }
}
