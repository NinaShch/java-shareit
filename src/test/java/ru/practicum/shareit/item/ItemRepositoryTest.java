package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ExtendWith(SpringExtension.class)
public class ItemRepositoryTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private ItemRepository repository;

    @Test
    public void contextLoads() {
        assertNotNull(em);
    }

    @Test
    void verifyBootstrappingByPersistingAnItem() {
        User owner = createUser("owner");
        Item item = createItem("item", owner);

        assertNull(item.getId());
        em.persist(owner);
        em.persist(item);
        assertNotNull(item.getId());
    }

    @Test
    void getItemOwner() {
        User owner = createUser("owner");
        Item item = createItem("item", owner);

        em.persist(owner);
        item = em.persist(item);

        User actualOwner = repository.getItemOwner(item.getId());
        assertEquals(owner, actualOwner);
    }

    @Test
    void search() {
        User owner = createUser("owner");
        Item item1 = createItem("test item", owner);
        Item item2 = createItem("TEST item", owner);
        Item item3 = createItem("teSt item", owner);
        Item item4 = createItem("item for TeSTing", owner);
        Item item5 = createItem("teXt item", owner);
        Item item6 = createItem("just another item", owner);

        em.persist(owner);
        em.persist(item1);
        em.persist(item2);
        em.persist(item3);
        em.persist(item4);
        em.persist(item5);
        em.persist(item6);

        List<Item> found = repository.search("test", Pageable.unpaged());
        assertThat(found, hasItems(item1, item2, item3, item4));
        assertThat(found, not(hasItems(item5, item6)));
    }

    @Test
    void findByOwner() {
        User owner = createUser("owner");
        User owner2 = createUser("owner2");
        Item item1 = createItem("item1", owner);
        Item item2 = createItem("item2", owner);
        Item item3 = createItem("item3", owner2);

        em.persist(owner);
        em.persist(owner2);
        em.persist(item1);
        em.persist(item2);
        em.persist(item3);

        List<Item> found = repository.findByOwner(owner, Pageable.unpaged());
        assertThat(found, hasItems(item1, item2));
        assertThat(found, not(hasItems(item3)));
    }

    @Test
    void findByRequestId() {
        User owner = createUser("owner");
        ItemRequest itemRequest = createItemRequest("request1");
        Item item = createItem("item1", owner);
        item.setRequest(itemRequest);

        em.persist(owner);
        itemRequest = em.persist(itemRequest);
        em.persist(item);

        List<Item> found = repository.findByRequestId(itemRequest.getId());
        assertThat(found, hasItems(item));
    }

    private Item createItem(String name, User owner) {
        Item item = new Item();
        item.setName(name);
        item.setDescription(name + " description");
        item.setAvailable(true);
        item.setOwner(owner);
        return item;
    }

    private User createUser(String name) {
        User user = new User();
        user.setName(name);
        user.setEmail(name + "@yandex.ru");
        return user;
    }

    private ItemRequest createItemRequest(String name) {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription(name);
        return itemRequest;
    }
}
