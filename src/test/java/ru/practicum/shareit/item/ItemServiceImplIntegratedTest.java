package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@ExtendWith(SpringExtension.class)
public class ItemServiceImplIntegratedTest {

    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemService service;

    @BeforeEach
    public void before() {
        itemRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    public void addNewItem() {
        User user = userRepository.save(UserMapper.toUser(makeUserDto("some@email.com", "Пётр Иванов")));

        ItemDto dto = makeItemDto("Чесалка", "Чешет");
        service.addNewItem(dto, user.getId());

        List<Item> items = itemRepository.findByOwner(user, Pageable.unpaged());
        assertEquals(1, items.size());
        Item item = items.get(0);

        verifyItem(dto, item);
    }

    @Test
    public void getUserItems() {
        User user = userRepository.save(UserMapper.toUser(makeUserDto("some@email.com", "Пётр Иванов")));

        ItemDto itemDto1 = makeItemDto("Чесалка", "Чешет");
        ItemDto itemDto2 = makeItemDto("Ковырялка", "Ковыряет");
        ItemDto itemDto3 = makeItemDto("Давилка", "Давит");

        itemRepository.save(ItemMapper.toItem(itemDto1, user, null));
        itemRepository.save(ItemMapper.toItem(itemDto2, user, null));
        itemRepository.save(ItemMapper.toItem(itemDto3, user, null));

        List<ItemDto> items = new ArrayList<>(service.getItemsByUserId(user.getId(), null, null));
        assertEquals(3, items.size());

        verifyItem(itemDto1, items.get(0));
        verifyItem(itemDto2, items.get(1));
        verifyItem(itemDto3, items.get(2));
    }

    private ItemDto makeItemDto(String name, String description) {
        return new ItemDto(null, name, description, true, null, null, null, null);
    }

    private UserDto makeUserDto(String email, String name) {
        return new UserDto(null, name, email);
    }

    private void verifyItem(ItemDto expected, Item actual) {
        assertThat(actual.getId(), notNullValue());
        assertThat(actual.getName(), equalTo(expected.getName()));
        assertThat(actual.getDescription(), equalTo(expected.getDescription()));
    }

    private void verifyItem(ItemDto expected, ItemDto actual) {
        assertThat(actual.getId(), notNullValue());
        assertThat(actual.getName(), equalTo(expected.getName()));
        assertThat(actual.getDescription(), equalTo(expected.getDescription()));
    }
}
