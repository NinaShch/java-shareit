package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@AutoConfigureTestDatabase
@ExtendWith(SpringExtension.class)
public class UserServiceImplIntegratedTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService service;

    @BeforeEach
    public void before() {
        userRepository.deleteAll();
    }

    @Test
    public void addUser() {
        UserDto userDto = makeUserDto("some@email.com", "Пётр Иванов");
        service.addUser(userDto);

        Optional<User> userOpt = userRepository.findByEmail("some@email.com");
        assertTrue(userOpt.isPresent());
        User user = userOpt.get();

        assertThat(user.getId(), notNullValue());
        assertThat(user.getName(), equalTo(userDto.getName()));
        assertThat(user.getEmail(), equalTo(userDto.getEmail()));
    }

    private UserDto makeUserDto(String email, String name) {
        return new UserDto(null, name, email);
    }
}
