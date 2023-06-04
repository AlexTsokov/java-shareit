package ru.practicum.shareit;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.InfoFromRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.dto.PageableRequest;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemServiceImplTest {

    private final EntityManager entityManager;
    private final ItemServiceImpl service;
    private final ItemRepository itemRepository;
    private User user;

    @BeforeEach
    void beforeEach() {
        user = new User();
        user.setName("name");
        user.setEmail("mail@email.ru");
        entityManager.persist(user);
    }

    @AfterEach
    void afterEach() {
        entityManager.createNativeQuery("truncate table users");
        entityManager.createNativeQuery("truncate table items");
        entityManager.createNativeQuery("truncate table bookings");
    }

    @Test
    void createItemTest() {
        Item item = Item.builder()
                .name("itemname")
                .description("description")
                .available(true)
                .build();

        Item itemService = service.createItem(user.getId(), item);

        Item itemFromDb = itemRepository.getReferenceById(item.getId());

        assertEquals(itemService.getId(), itemFromDb.getId());
        assertEquals(itemService.getName(), itemFromDb.getName());
        assertEquals(itemService.getOwner().getName(), user.getName());
        assertEquals(itemService.getDescription(), itemFromDb.getDescription());
        assertEquals(itemService.getAvailable(), itemFromDb.getAvailable());
    }

    @Test
    void updateItemTest() {
        Item item = Item.builder()
                .name("itemname")
                .description("description")
                .available(true)
                .build();

        item = service.createItem(user.getId(), item);

        Item updated = Item.builder()
                .name("itemNewname")
                .description("description new")
                .available(true)
                .build();

        updated = service.updateItem(user.getId(), item.getId(), updated);

        Item updatedItemFromDb = itemRepository.getReferenceById(item.getId());

        assertEquals(updatedItemFromDb.getId(), updated.getId());
        assertEquals(updatedItemFromDb.getName(), updated.getName());
        assertEquals(updatedItemFromDb.getOwner().getId(), user.getId());
        assertEquals(updatedItemFromDb.getDescription(), updated.getDescription());
        assertEquals(updatedItemFromDb.getAvailable(), updated.getAvailable());
    }

    @Test
    void findItemDtoByIdTest() {
        Item item = Item.builder()
                .name("itemname")
                .description("description")
                .available(true)
                .build();

        item = service.createItem(user.getId(), item);

        Item result = service.findItemById(item.getId());

        Item itemFromDB = itemRepository.findById(item.getId()).orElseThrow(EntityNotFoundException::new);

        assertEquals(itemFromDB.getId(), result.getId());
        assertEquals(itemFromDB.getName(), result.getName());
        assertEquals(itemFromDB.getOwner().getId(), user.getId());
        assertEquals(itemFromDB.getAvailable(), result.getAvailable());
        assertEquals(item.getDescription(), result.getDescription());
    }

    @Test
    void getAllUserItemsTest() {
        Item item = Item.builder()
                .name("itemname")
                .description("description")
                .available(true)
                .build();

        item = service.createItem(user.getId(), item);

        Item item2 = Item.builder()
                .name("itemname2")
                .description("description 2")
                .available(true)
                .build();

        item2 = service.createItem(user.getId(), item2);

        InfoFromRequest info = InfoFromRequest.getInfoFromRequest(user.getId(), 0, 10);

        List<ItemDto> items = service.getAllUserItems(info);

        List<ItemDto> itemsFromDB = ItemMapper.toDtoList(itemRepository.findItemsByOwner(user.getId()));

        assertEquals(items.size(), 2);
        assertEquals(items.get(0).getId(), itemsFromDB.get(0).getId());
        assertEquals(items.get(0).getName(), itemsFromDB.get(0).getName());
        assertEquals(items.get(0).getDescription(), itemsFromDB.get(0).getDescription());
        assertEquals(items.get(0).getAvailable(), itemsFromDB.get(0).getAvailable());
        assertEquals(items.get(1).getId(), itemsFromDB.get(1).getId());
        assertEquals(items.get(1).getName(), itemsFromDB.get(1).getName());
        assertEquals(items.get(1).getDescription(), itemsFromDB.get(1).getDescription());
        assertEquals(items.get(1).getAvailable(), itemsFromDB.get(1).getAvailable());
    }

    @Test
    void searchItems() {
        Item item = Item.builder()
                .name("drill")
                .description("description drill")
                .available(true)
                .build();

        item = service.createItem(user.getId(), item);

        Item item2 = Item.builder()
                .name("playstation")
                .description("description games")
                .available(true)
                .build();

        item2 = service.createItem(user.getId(), item2);

        InfoFromRequest infoFromRequest1 = InfoFromRequest.getInfoFromRequestWithText(
                "drill".toUpperCase(), 0, 10);
        InfoFromRequest infoFromRequest2 = InfoFromRequest.getInfoFromRequestWithText(
                "play".toUpperCase(), 0, 10);

        List<Item> items = service.searchItems(infoFromRequest1);
        List<Item> items2 = service.searchItems(infoFromRequest2);
        Pageable pageable1 = PageableRequest.getPageableRequest(
                infoFromRequest1.getFromPage(), infoFromRequest1.getSizePages());
        Pageable pageable2 = PageableRequest.getPageableRequest(
                infoFromRequest2.getFromPage(), infoFromRequest2.getSizePages());

        List<ItemDto> itemsFromDBDrill = ItemMapper.toDtoList(itemRepository.search(
                "drill".toUpperCase(), pageable1));
        List<ItemDto> itemsFromDBPlayStation = ItemMapper.toDtoList(itemRepository.search(
                "play".toUpperCase(), pageable2));

        assertEquals(items.size(), itemsFromDBDrill.size());
        assertEquals(items.get(0).getId(), itemsFromDBDrill.get(0).getId());
        assertEquals(items.get(0).getName(), itemsFromDBDrill.get(0).getName());
        assertEquals(items.get(0).getDescription(), itemsFromDBDrill.get(0).getDescription());
        assertEquals(items.get(0).getAvailable(), itemsFromDBDrill.get(0).getAvailable());
        assertEquals(items2.get(0).getId(), itemsFromDBPlayStation.get(0).getId());
        assertEquals(items2.get(0).getName(), itemsFromDBPlayStation.get(0).getName());
        assertEquals(items2.get(0).getDescription(), itemsFromDBPlayStation.get(0).getDescription());
        assertEquals(items2.get(0).getAvailable(), itemsFromDBPlayStation.get(0).getAvailable());

    }

}