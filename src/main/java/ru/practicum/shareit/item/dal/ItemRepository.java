package ru.practicum.shareit.item.dal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findByOwnerId(long ownerId);

    @Query("""
            SELECT i FROM Item i
            WHERE (UPPER(i.name) LIKE UPPER(CONCAT('%', ?1, '%'))
            OR UPPER(i.description) LIKE UPPER(CONCAT('%', ?1, '%')))
            AND i.available = true
            """)
    List<Item> searchItems(String text);

}
