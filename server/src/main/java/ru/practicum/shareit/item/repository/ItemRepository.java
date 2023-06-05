package ru.practicum.shareit.item.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    @Query(" select i from Item i " +
            "where (upper(i.name) like concat('%', ?1, '%') " +
            " or upper(i.description) like concat('%', ?1, '%')) and i.available = true")
    List<Item> search(String text, Pageable pageable);

    @Query(" select i from Item i where i.owner.id = ?1 order by i.name desc")
    List<Item> findItemsByOwner(Long ownerId, Pageable pageable);

    @Query(" select i from Item i where i.owner.id = ?1")
    List<Item> findItemsByOwner(Long userId);

    List<Item> getItemsByRequestId(Long requestId);

    List<Item> getItemsByRequestIdIn(Collection<Long> ids);

}
