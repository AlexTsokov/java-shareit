package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    @Query(" select i from Item i " +
            "where (upper(i.name) like concat('%', ?1, '%') " +
            " or upper(i.description) like concat('%', ?1, '%')) and i.available = true")
    List<Item> search(String text);

    @Query(" select i from Item i where i.owner.id = ?1")
    List<Item> findItemsByUser(Long userId);

}
