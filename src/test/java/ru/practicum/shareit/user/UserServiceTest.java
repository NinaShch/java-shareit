package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.AssertionErrors.assertEquals;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class UserServiceTest {

    @InjectMocks
    private UserServiceImpl underTest;

    @Mock
    UserRepository userRepository;

    @BeforeEach
    public void before() {
        when(userRepository.save(Matchers.any())).thenAnswer(input -> input.getArguments()[0]);
    }

    @Test
    public void bad_request_when_no_email() {
        assertThrows(
                BadRequestException.class,
                () -> {
                    UserDto userDto = new UserDto(null, "user", null);
                    underTest.addUser(userDto);
                }
        );
    }

    @Test
    public void bad_request_when_empty_email() {
        assertThrows(
                BadRequestException.class,
                () -> {
                    UserDto userDto = new UserDto(null, "user", "");
                    underTest.addUser(userDto);
                }
        );
    }

    @Test
    public void bad_request_when_blank_email() {
        assertThrows(
                BadRequestException.class,
                () -> {
                    UserDto userDto = new UserDto(null, "user", "   ");
                    underTest.addUser(userDto);
                }
        );
    }

    @Test
    public void saved_to_repo_when_add_new() {
        UserDto userDto = new UserDto(null, "user", "user@yandex.ru");
        underTest.addUser(userDto);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());

        User capturedUser = captor.getValue();
        assertEquals("names not match", "user", capturedUser.getName());
        assertEquals("mails not match", "user@yandex.ru", capturedUser.getEmail());
    }

    @Test
    public void return_added_when_add_new() {
        UserDto userDto = new UserDto(null, "user", "user@yandex.ru");
        UserDto result = underTest.addUser(userDto);

        assertEquals("names not match", "user", result.getName());
        assertEquals("mails not match", "user@yandex.ru", result.getEmail());
    }

    @Test
    public void conflict_exception_when_repository_throws() {
        assertThrows(
                ConflictException.class,
                () -> {
                    when(userRepository.save(Matchers.any())).thenThrow(new RuntimeException());
                    UserDto userDto = new UserDto(null, "user", "user@yandex.ru");
                    underTest.addUser(userDto);
                }
        );
    }

    @Test
    public void bad_request_when_updating_missing_user() {
        assertThrows(
                BadRequestException.class,
                () -> {
                    when(userRepository.findById(eq(123L))).thenReturn(Optional.empty());
                    UserDto userDto = new UserDto(null, "user", "user@yandex.ru");
                    underTest.updateUser(userDto, 123L);
                }
        );
    }

    @Test
    public void saved_to_repo_when_updated() {
        User user = new User(123L, "user_old", "user_old@yandex.ru");
        when(userRepository.findById(eq(123L))).thenReturn(Optional.of(user));
        UserDto userDto = new UserDto(null, "user_new", "user_new@yandex.ru");
        underTest.updateUser(userDto, 123L);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());

        User capturedUser = captor.getValue();
        assertEquals("names not match", "user_new", capturedUser.getName());
        assertEquals("mails not match", "user_new@yandex.ru", capturedUser.getEmail());
        assertEquals("ids not match", (Long) 123L, capturedUser.getId());
    }

    @Test
    public void return_changed_one_when_updated() {
        User user = new User(123L, "user_old", "user_old@yandex.ru");
        when(userRepository.findById(eq(123L))).thenReturn(Optional.of(user));
        UserDto userDto = new UserDto(null, "user_new", "user_new@yandex.ru");
        UserDto result = underTest.updateUser(userDto, 123L);

        assertEquals("names not match", "user_new", result.getName());
        assertEquals("mails not match", "user_new@yandex.ru", result.getEmail());
        assertEquals("ids not match", (Long) 123L, result.getId());
    }

    @Test
    public void get_all_users_gets() {
        when(userRepository.findAll()).thenReturn(
                Stream.of(
                        new User(1L, "user1", "user1@yandex.ru"),
                        new User(2L, "user2", "user2@yandex.ru"),
                        new User(3L, "user3", "user3@yandex.ru")
                ).collect(Collectors.toList())
        );
        Collection<UserDto> result = underTest.getAllUsers();

        assertThat(result, hasItems(
                new UserDto(1L, "user1", "user1@yandex.ru"),
                new UserDto(2L, "user2", "user2@yandex.ru"),
                new UserDto(3L, "user3", "user3@yandex.ru")
        ));
    }


    @Test
    public void get_userById_throws_when_not_found() {
        assertThrows(
                NotFoundException.class,
                () -> {
                    when(userRepository.findById(eq(123L))).thenReturn(Optional.empty());
                    underTest.getUserById(123L);
                }
        );
    }

    @Test
    public void get_userById_gets_when_found() {
        User user = new User(123L, "user", "user@yandex.ru");
        when(userRepository.findById(eq(123L))).thenReturn(Optional.of(user));
        UserDto result = underTest.getUserById(123L);

        assertEquals("names not match", "user", result.getName());
        assertEquals("mails not match", "user@yandex.ru", result.getEmail());
        assertEquals("ids not match", (Long) 123L, result.getId());
    }

    @Test
    public void delete_userById_deletes() {
        underTest.deleteUserById(123L);
        verify(userRepository).deleteById(123L);
    }
}
