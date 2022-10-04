package ru.practicum.shareit.item.storage;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    @Query("select i from Item i " +
            "where (upper(i.name) like upper(concat('%', ?1, '%')) " +
            "   or upper(i.description) like upper(concat('%', ?1, '%'))) and i.available = true")
    List<Item> search(@Param("text") String text, Pageable pageable);

    @Query("select i.owner from Item i where i.id = ?1")
    User getItemOwner(Long itemId);

    List<Item> findByOwner(User owner, Pageable pageable);

    List<Item> findByRequestId(Long requsetId);
}
