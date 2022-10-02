package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.paging.OffsetLimitPageable;
import ru.practicum.shareit.user.model.User;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
@ExtendWith(SpringExtension.class)
public class BookingRepositoryTest {
    private static final LocalDateTime START = LocalDateTime.of(2032, 9, 15, 9, 19);
    private static final LocalDateTime END = LocalDateTime.of(2033, 1, 1, 0, 0);

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private TestEntityManager em;

    @Autowired
    private BookingRepository repository;

    @Test
    public void contextLoads() {
        assertNotNull(em);
    }

    @Test
    public void findByBooker() {
        User owner = createUser("owner");
        User booker1 = createUser("booker1");
        User booker2 = createUser("booker2");
        Item item1 = createItem("item1", owner);
        Item item2 = createItem("item2", owner);
        Booking booking1 = createBooking(item1, booker1);
        Booking booking2 = createBooking(item2, booker2);

        em.persist(owner);
        em.persist(booker1);
        em.persist(booker2);
        em.persist(item1);
        em.persist(item2);
        em.persist(booking1);
        em.persist(booking2);

        List<Booking> found = repository.findByBooker(booker1, OffsetLimitPageable.unpaged());
        assertThat(found, hasItems(booking1));
        assertThat(found, not(hasItems(booking2)));
    }

    @Test
    public void findCurrentByBooker() {
        User owner = createUser("owner");
        User booker1 = createUser("booker1");
        User booker2 = createUser("booker2");
        Item item1 = createItem("item1", owner);
        Item item2 = createItem("item2", owner);
        LocalDateTime now = LocalDateTime.now();
        Booking booking1a = createBooking(item1, booker1, now.minus(Duration.ofDays(1)), now.plus(Duration.ofDays(1)));
        Booking booking1b = createBooking(item1, booker1, now.minus(Duration.ofDays(300)), now.minus(Duration.ofDays(200)));
        Booking booking1c = createBooking(item1, booker1, now.plus(Duration.ofDays(300)), now.plus(Duration.ofDays(200)));
        Booking booking2a = createBooking(item2, booker2, now.minus(Duration.ofDays(1)), now.plus(Duration.ofDays(1)));
        Booking booking2b = createBooking(item2, booker2, now.minus(Duration.ofDays(300)), now.minus(Duration.ofDays(200)));
        Booking booking2c = createBooking(item2, booker2, now.plus(Duration.ofDays(300)), now.plus(Duration.ofDays(200)));

        em.persist(owner);
        em.persist(booker1);
        em.persist(booker2);
        em.persist(item1);
        em.persist(item2);
        em.persist(booking1a);
        em.persist(booking1b);
        em.persist(booking1c);
        em.persist(booking2a);
        em.persist(booking2b);
        em.persist(booking2c);

        List<Booking> found = repository.findCurrentByBooker(booker1, OffsetLimitPageable.unpaged());
        assertThat(found, hasItems(booking1a));
        assertThat(found, not(hasItems(booking1b, booking1c, booking2a, booking2b, booking2c)));
    }

    @Test
    public void findByOwner() {
        User owner1 = createUser("owner1");
        User owner2 = createUser("owner2");
        User booker = createUser("booker");
        Item item1 = createItem("item1", owner1);
        Item item2 = createItem("item2", owner2);
        Booking booking1 = createBooking(item1, booker);
        Booking booking2 = createBooking(item2, booker);

        em.persist(owner1);
        em.persist(owner2);
        em.persist(booker);
        em.persist(item1);
        em.persist(item2);
        em.persist(booking1);
        em.persist(booking2);

        List<Booking> found = repository.findByOwner(owner1, OffsetLimitPageable.unpaged());
        assertThat(found, hasItems(booking1));
        assertThat(found, not(hasItems(booking2)));
    }

    @Test
    public void findCurrentByOwner() {

        User owner = createUser("owner");
        User booker1 = createUser("booker1");
        User booker2 = createUser("booker2");
        Item item1 = createItem("item1", owner);
        Item item2 = createItem("item2", owner);
        LocalDateTime now = LocalDateTime.now();
        Booking booking1a = createBooking(item1, booker1, now.minus(Duration.ofDays(1)), now.plus(Duration.ofDays(1)));
        Booking booking1b = createBooking(item1, booker1, now.minus(Duration.ofDays(300)), now.minus(Duration.ofDays(200)));
        Booking booking1c = createBooking(item1, booker1, now.plus(Duration.ofDays(300)), now.plus(Duration.ofDays(200)));
        Booking booking2a = createBooking(item2, booker2, now.minus(Duration.ofDays(1)), now.plus(Duration.ofDays(1)));
        Booking booking2b = createBooking(item2, booker2, now.minus(Duration.ofDays(300)), now.minus(Duration.ofDays(200)));
        Booking booking2c = createBooking(item2, booker2, now.plus(Duration.ofDays(300)), now.plus(Duration.ofDays(200)));

        em.persist(owner);
        em.persist(booker1);
        em.persist(booker2);
        em.persist(item1);
        em.persist(item2);
        em.persist(booking1a);
        em.persist(booking1b);
        em.persist(booking1c);
        em.persist(booking2a);
        em.persist(booking2b);
        em.persist(booking2c);

        List<Booking> found = repository.findCurrentByOwner(owner, OffsetLimitPageable.unpaged());
        assertThat(found, hasItems(booking1a));
        assertThat(found, not(hasItems(booking1b, booking1c, booking2a, booking2b, booking2c)));
    }

    private User createUser(String name) {
        User user = new User();
        user.setName(name);
        user.setEmail(name + "@yandex.ru");
        return user;
    }

    private Item createItem(String name, User owner) {
        Item item = new Item();
        item.setName(name);
        item.setDescription(name + " description");
        item.setAvailable(true);
        item.setOwner(owner);
        return item;
    }

    private Booking createBooking(Item item, User booker, LocalDateTime start, LocalDateTime end) {
        Booking booking = new Booking();
        booking.setStart(start);
        booking.setEnd(end);
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(Status.APPROVED);
        return booking;
    }

    private Booking createBooking(Item item, User booker) {
        return createBooking(item, booker, START, END);
    }
}
